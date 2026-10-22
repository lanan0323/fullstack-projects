// 声明当前类所属的包路径，位于trade模块的service.impl子包下
package com.debuff.trade.service.impl;

// ==================== 导入Java标准库类 ====================
// 导入BigDecimal类，用于精确的十进制金额计算，避免浮点数精度丢失
import java.math.BigDecimal;
// 导入RoundingMode枚举，用于指定BigDecimal的舍入模式（如四舍五入）
import java.math.RoundingMode;
// 导入List集合接口，用于方法返回多条记录的列表
import java.util.List;
// 导入Random类，用于生成订单号中的随机数部分
import java.util.Random;

// ==================== 导入公共模块类 ====================
// 导入自定义业务异常类，用于在业务逻辑出错时抛出异常触发事务回滚
import com.debuff.common.exception.ServiceException;
// 导入日期工具类，提供获取当前时间和格式化日期的静态方法
import com.debuff.common.utils.DateUtils;

// ==================== 导入Spring框架注解 ====================
// 导入Autowired注解，用于声明依赖注入，Spring容器会自动注入对应的Bean
import org.springframework.beans.factory.annotation.Autowired;
// 导入Service注解，将当前类标记为Spring的Service组件，由容器管理生命周期
import org.springframework.stereotype.Service;
// 导入事务注解，用于声明式事务管理，保证方法内多步操作的原子性
import org.springframework.transaction.annotation.Transactional;

// ==================== 导入本模块Mapper层（数据访问层） ====================
// 导入订单Mapper接口，提供对debuff_order表的增删改查SQL操作
import com.debuff.trade.mapper.OrderMapper;
// 导入上架记录Mapper接口，提供对debuff_skin_listing表的SQL操作
import com.debuff.trade.mapper.ListingMapper;

// ==================== 导入本模块Domain层（实体类） ====================
// 导入Order实体类，对应数据库中的debuff_order订单表
import com.debuff.trade.domain.Order;
// 导入Listing实体类，对应数据库中的debuff_skin_listing上架记录表
import com.debuff.trade.domain.Listing;
// 导入Inventory实体类，对应数据库中的debuff_inventory库存表
import com.debuff.trade.domain.entity.Inventory;

// ==================== 导入本模块Service层（业务接口） ====================
// 导入订单Service接口，当前实现类需要实现该接口定义的方法
import com.debuff.trade.service.IOrderService;
// 导入库存Service接口，提供库存的增删改查及状态更新方法
import com.debuff.trade.service.IInventoryService;
// 导入钱包Service接口，提供余额冻结、解冻、入账等资金操作方法
import com.debuff.trade.service.IWalletService;

/**
 * 交易订单Service业务层处理
 *
 * <p>本模块是整个Debuff平台最核心的交易链路，一次完整的下单操作涉及
 * 钱包（debuff_user_wallet）、库存（debuff_inventory）、上架（debuff_skin_listing）、
 * 订单（debuff_order）四张表的联动操作。</p>
 *
 * <h3>事务设计</h3>
 * <ul>
 *   <li>createOrder / confirmOrder / cancelOrder 三个核心方法均标注
 *       {@code @Transactional(rollbackFor = Exception.class)}，保证四步操作
 *       要么全部成功，要么全部回滚，不会出现"钱扣了货没锁"的中间态。</li>
 *   <li>并发安全采用<b>条件更新</b>（乐观锁思路）：SQL 中带有 WHERE status=原状态，
 *       只有第一个执行的线程能成功修改，其余线程返回0行后主动抛异常触发回滚。
 *       相比"先查再改"，消除了查询与更新之间的时间窗口隐患。</li>
 * </ul>
 *
 * <h3>订单状态流转</h3>
 * <pre>
 *   下单 ──► 0:待确认（已冻结） ──┬──► 1:已完成（确认收货）
 *                                └──► 2:已取消（手动取消 / 定时任务超时取消）
 * </pre>
 *
 * <h3>【答辩要点】为什么 rollbackFor = Exception.class？</h3>
 * <p>Spring @Transactional 默认只在抛出 RuntimeException 及其子类时才回滚。
 * 如果方法抛出了 checked exception（如 IOException），默认行为是<b>不回滚</b>！
 * 这会导致数据不一致。加上 rollbackFor = Exception.class 后，无论 checked 还是
 * unchecked 异常都会触发回滚，更安全。本项目中 ServiceException 继承自 RuntimeException，
 * 理论上不加也能回滚，但加上是最佳实践，防止未来代码变更引入 checked exception 时遗漏。</p>
 *
 * <h3>【答辩要点】为什么用条件更新而不是悲观锁（SELECT FOR UPDATE）？</h3>
 * <p>悲观锁：先 SELECT ... FOR UPDATE 锁住行，再 UPDATE。缺点：
 * ① 加锁范围大，锁住整行直到事务结束，并发性能差；
 * ② 有死锁风险（两个事务交叉锁不同行时）；
 * ③ 需要额外的查询语句，多一次网络往返。
 * 条件更新：直接 UPDATE ... WHERE status=原状态，利用数据库行锁的原子性，
 * 一条 SQL 完成"检查+修改"，返回0行就知道冲突了。更轻量、无死锁风险。</p>
 *
 * <h3>【答辩要点】createOrder 调用了 walletService.freezePay，两个方法都有 @Transactional，事务怎么传播？</h3>
 * <p>Spring @Transactional 默认传播行为是 REQUIRED：如果当前已有事务，就加入该事务；
 * 如果没有，就新建一个。所以 createOrder 调用 walletService.freezePay 时，
 * freezePay 会<b>加入</b> createOrder 的事务，两者在同一个数据库连接中执行。
 * 如果 freezePay 抛异常，异常会沿着调用栈向上传播，导致 createOrder 的事务也回滚。
 * 这就是 Spring AOP 代理的事务传播机制——同一个事务内，任一环节失败，全部回滚。</p>
 *
 * @author debuff
 * @date 2026-08-25
 */
// 标记为Spring的Service组件，Spring容器启动时会自动扫描并创建该类的实例
@Service
// 定义订单服务实现类，实现IOrderService接口中声明的所有方法
public class OrderServiceImpl implements IOrderService 
{
    /** 订单状态：待确认(已付款冻结) */
    // 定义订单状态常量0，表示订单已创建且买家余额已冻结，等待卖家发货或确认
    private static final int STATUS_PENDING = 0;

    /** 订单状态：已完成 */
    // 定义订单状态常量1，表示买家已确认收货，交易完成，资金已结算
    private static final int STATUS_FINISHED = 1;

    /** 订单状态：已取消 */
    // 定义订单状态常量2，表示订单已被取消（用户手动取消或定时任务超时取消），冻结金额已退回
    private static final int STATUS_CANCELLED = 2;

    /** 平台手续费比例 */
    // 定义平台手续费率为5%，即每笔交易收取成交价的5%作为平台服务费
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");

    // 使用Autowired注解自动注入订单Mapper实例，用于执行订单表的SQL操作
    @Autowired
    private OrderMapper orderMapper;

    // 使用Autowired注解自动注入上架记录Mapper实例，用于执行上架表的SQL操作
    @Autowired
    private ListingMapper listingMapper;

    // 使用Autowired注解自动注入钱包Service实例，用于执行余额冻结/解冻/入账等资金操作
    @Autowired
    private IWalletService walletService;

    // 使用Autowired注解自动注入库存Service实例，用于执行库存的查询和状态变更操作
    @Autowired
    private IInventoryService inventoryService;

    /**
     * 查询交易订单
     * 
     * @param id 交易订单主键
     * @return 交易订单
     */
    // 重写接口方法，根据主键ID查询单个订单详情
    @Override
    public Order selectOrderById(Long id)
    {
        // 调用订单Mapper的查询方法，根据主键ID从数据库中查询对应的订单记录并返回
        return orderMapper.selectOrderById(id);
    }

    /**
     * 查询交易订单列表
     * 
     * @param order 交易订单
     * @return 交易订单
     */
    // 重写接口方法，根据条件查询订单列表（支持按字段模糊查询）
    @Override
    public List<Order> selectOrderList(Order order)
    {
        // 调用订单Mapper的列表查询方法，传入条件对象，返回符合条件的订单列表
        return orderMapper.selectOrderList(order);
    }

    /**
     * 新增交易订单
     * 
     * @param order 交易订单
     * @return 结果
     */
    // 重写接口方法，新增一条订单记录到数据库
    @Override
    public int insertOrder(Order order)
    {
        // 设置订单的创建时间为当前系统时间，使用日期工具类获取
        order.setCreateTime(DateUtils.getNowDate());
        // 调用订单Mapper的插入方法，将订单对象写入数据库，返回影响的行数
        return orderMapper.insertOrder(order);
    }

    /**
     * 修改交易订单
     * 
     * @param order 交易订单
     * @return 结果
     */
    // 重写接口方法，根据订单对象中的字段更新数据库中的订单记录
    @Override
    public int updateOrder(Order order)
    {
        // 设置订单的更新时间为当前系统时间，记录最后一次修改的时间戳
        order.setUpdateTime(DateUtils.getNowDate());
        // 调用订单Mapper的更新方法，将订单对象的最新数据写入数据库，返回影响的行数
        return orderMapper.updateOrder(order);
    }

    /**
     * 批量删除交易订单
     * 
     * @param ids 需要删除的交易订单主键
     * @return 结果
     */
    // 重写接口方法，根据传入的ID数组批量删除订单记录
    @Override
    public int deleteOrderByIds(Long[] ids)
    {
        // 调用订单Mapper的批量删除方法，传入ID数组，返回实际删除的记录数
        return orderMapper.deleteOrderByIds(ids);
    }

    /**
     * 删除交易订单信息
     * 
     * @param id 交易订单主键
     * @return 结果
     */
    // 重写接口方法，根据单个ID删除一条订单记录
    @Override
    public int deleteOrderById(Long id)
    {
        // 调用订单Mapper的单条删除方法，传入订单ID，返回影响的行数
        return orderMapper.deleteOrderById(id);
    }

    /**
     * 用户下单购买：冻结买家余额 -> 冻结库存 -> 标记上架已售 -> 生成订单
     *
     * <p>事务保证：四步操作在同一个 {@code @Transactional} 中，任一步抛异常则全部回滚。</p>
     * <p>并发安全：第2、3步使用条件更新（WHERE status=原状态），两人同时买同一商品时，
     * 只有第一个执行的线程能成功，另一个返回0行走回滚，前端提示"商品已售出"。</p>
     *
     * @param listingId 上架记录ID（市场详情页的在售列表项）
     * @param buyerId   买家用户ID（从 SecurityUtils 获取）
     * @return 生成的订单对象
     */
    // 重写接口方法，执行下单操作
    @Override
    // 开启声明式事务，rollbackFor指定所有异常（包括checked异常）都触发回滚，保证数据一致性
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long listingId, Long buyerId)
    {
        // ==================== 前置校验阶段 ====================
        // 查询上架记录，校验商品存在且状态为在售（status=0）
        // 根据上架记录ID从数据库查询对应的Listing对象，包含价格、卖家ID、库存ID等信息
        Listing listing = listingMapper.selectListingById(listingId);
        // 判断查询结果是否为空，为空说明该上架记录不存在
        if (listing == null)
        {
            // 抛出业务异常，提示商品不存在，事务会自动回滚
            throw new ServiceException("商品不存在");
        }
        // 判断上架记录的状态是否不为0（即不在售），防止购买已售出或已下架的商品
        if (listing.getStatus() != 0)
        {
            // 抛出业务异常，提示商品已售出或已下架，事务回滚
            throw new ServiceException("商品已售出或已下架");
        }
        // 校验买家不能是卖家自己（防止自己买自己的饰品）
        // 使用equals比较买家ID和卖家ID是否相同，防止刷单或异常操作
        if (buyerId.equals(listing.getSellerId()))
        {
            // 抛出业务异常，提示不能购买自己发布的商品，事务回滚
            throw new ServiceException("不能购买自己发布的商品");
        }
        // 校验库存归属与状态，防止上架记录与库存数据不一致的脏数据
        // 库存状态必须为1（在售），且库存的userId必须与上架记录的sellerId一致
        // 根据上架记录关联的库存ID，从数据库查询对应的库存对象
        Inventory inventory = inventoryService.selectInventoryById(listing.getInventoryId());
        // 判断库存是否存在且状态是否为1（在售），不满足说明商品已被下架
        if (inventory == null || inventory.getStatus() != 1)
        {
            // 抛出业务异常，提示商品已下架，事务回滚
            throw new ServiceException("商品已下架");
        }
        // 判断库存所属用户ID是否与上架记录的卖家ID一致，防止数据不一致
        if (!inventory.getUserId().equals(listing.getSellerId()))
        {
            // 抛出业务异常，提示商品数据异常，事务回滚
            throw new ServiceException("商品数据异常，请联系管理员");
        }

        // ==================== 金额计算阶段 ====================
        BigDecimal price = listing.getPrice();
        // 平台手续费 = 价格 × 5%，使用 BigDecimal 的 multiply + setScale 保证精度到分
        // 不能用 double，否则浮点数精度丢失会导致金额计算错误（如 0.1+0.2≠0.3）
        // RoundingMode.HALF_UP 表示四舍五入
        // 计算平台手续费：价格乘以费率后保留2位小数，采用四舍五入方式
        BigDecimal platformFee = price.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        // 卖家实收 = 成交价 - 平台手续费
        // 计算卖家实际到手金额：用成交价格减去平台手续费
        BigDecimal sellerIncome = price.subtract(platformFee);
        // 生成唯一订单号：ORD + yyyyMMddHHmmss + 4位随机数
        // 调用私有方法生成全局唯一的订单编号，用于标识本次交易
        String orderNo = generateOrderNo();

        // ==================== 核心四步操作（事务内） ====================

        // 【答辩要点】为什么要"冻结"而不是直接扣余额？
        // 因为下单后还有可能取消（用户手动取消 / 定时任务超时取消）。
        // 如果直接扣了余额，取消时还得加回去，而且如果期间卖家已经确认收货了，
        // 退款逻辑会很复杂。用"冻结"设计：
        //   下单时：balance -= price, frozen += price（钱从可用变成冻结）
        //   确认收货：frozen -= price（冻结清零，钱真正支出）
        //   取消订单：frozen -= price, balance += price（冻结退回余额）
        // 这样无论确认还是取消，都能从冻结状态出发做逆向操作，逻辑清晰。
        // 类似支付宝的"预授权"机制。

        // 第1步：冻结买家钱包
        // 注意：这里不是直接扣余额，而是"余额减少、冻结金额增加"
        // 这样做的好处：如果后续取消订单，还能将冻结金额退回给买家
        // 如果余额不足，walletService 内部会抛出 ServiceException，事务直接回滚
        // 调用钱包Service的冻结支付方法，将买家账户中对应金额从可用余额转入冻结金额
        walletService.freezePay(buyerId, price, orderNo);

        // 第2步：冻结库存（条件更新）
        // SQL: UPDATE debuff_inventory SET status=2 WHERE id=? AND status=1
        // 返回0行说明状态已经被其他线程改变（比如被其他买家抢先购买），直接回滚
        // 这是防止并发问题的关键手段——利用数据库行锁保证原子性
        // 调用库存Service的条件更新方法，将库存状态从1（在售）改为2（冻结），返回0表示更新失败（已被他人抢先购买）
        if (inventoryService.updateStatus(inventory.getId(), 1, 2) == 0)
        {
            // 更新失败说明商品状态已被其他线程改变，抛出异常触发事务回滚
            throw new ServiceException("商品状态已变化，请刷新后重试");
        }

        // 第3步：标记上架记录为已售（条件更新）
        // SQL: UPDATE debuff_skin_listing SET status=1, sold_time=sysdate() WHERE id=? AND status=0
        // 同样，返回0行说明状态已变化，回滚
        // 调用上架记录Mapper的标记售出方法，将上架状态从0（在售）改为1（已售），返回0表示更新失败
        if (listingMapper.markSold(listingId) == 0)
        {
            // 更新失败说明上架记录状态已被改变，抛出异常触发事务回滚
            throw new ServiceException("商品状态已变化，请刷新后重试");
        }

        // 第4步：生成订单记录
        // 订单中快照了 inventoryId 和 listingId，供后续确认收货/取消时反查原始数据
        // 创建一个新的Order实体对象，用于封装本次交易的所有信息
        Order order = new Order();
        // 设置订单编号，使用前面生成的唯一订单号
        order.setOrderNo(orderNo);
        // 设置饰品ID，从上架记录中获取，记录本次交易的是哪个饰品
        order.setSkinId(listing.getSkinId());
        // 设置库存ID，从库存对象中获取，用于后续确认/取消时定位库存记录
        order.setInventoryId(inventory.getId());
        // 设置上架记录ID，关联原始的上架记录，便于追溯
        order.setListingId(listingId);
        // 设置买家用户ID，即当前下单的用户
        order.setBuyerId(buyerId);
        // 设置卖家用户ID，从上架记录中获取商品的发布者ID
        order.setSellerId(listing.getSellerId());
        // 设置成交总价，即商品的标价
        order.setPrice(price);
        // 设置平台手续费金额，即前面计算的成交价5%
        order.setPlatformFee(platformFee);
        // 设置卖家实收金额，即成交价减去平台手续费后的金额
        order.setSellerIncome(sellerIncome);
        // 设置订单状态为待确认（0），表示已冻结买家余额，等待确认收货或取消
        order.setStatus(STATUS_PENDING); // 0:待确认
        // 设置订单创建时间为当前系统时间
        order.setCreateTime(DateUtils.getNowDate());
        // 调用订单Mapper的插入方法，将封装好的订单对象持久化到数据库
        orderMapper.insertOrder(order);
        // 返回生成的订单对象，供Controller层返回给前端展示
        return order;
    }

    /**
     * 买家确认收货：订单置已完成 -> 买家解除冻结 -> 卖家入账 -> 饰品转移给买家
     *
     * <p>确认收货后资金链路：买家冻结金额扣除（钱已支出），卖家收到扣除手续费后的实收金额。
     * 饰品链路：卖家库存删除，为买家创建新库存（source=purchase），完成饰品所有权转移。</p>
     *
     * @param orderId 订单主键
     * @return 1 成功
     */
    // 重写接口方法，执行买家确认收货操作，完成资金结算和饰品转移
    @Override
    // 开启声明式事务，所有异常都触发回滚，保证确认收货四步操作的原子性
    @Transactional(rollbackFor = Exception.class)
    public int confirmOrder(Long orderId)
    {
        // ==================== 前置校验 ====================
        // 根据订单ID从数据库查询对应的订单记录
        Order order = orderMapper.selectOrderById(orderId);
        // 判断订单是否存在，为空说明传入的订单ID无效
        if (order == null)
        {
            // 抛出业务异常，提示订单不存在，事务回滚
            throw new ServiceException("订单不存在");
        }
        // 只有"待确认"（status=0）的订单才能确认收货，已完成/已取消的不允许重复操作
        // 判断订单当前状态是否为待确认状态，防止重复确认或非法操作
        if (order.getStatus() != STATUS_PENDING)
        {
            // 状态不匹配，抛出异常提示不允许确认收货，事务回滚
            throw new ServiceException("订单状态不允许确认收货");
        }

        // ==================== 核心四步操作（事务内） ====================

        // 第1步：订单状态 0→1（条件更新）
        // SQL: UPDATE debuff_order SET status=1 WHERE id=? AND status=0
        // 防止买家和定时任务同时确认/取消同一订单的并发问题
        // 返回0行说明状态已被其他线程改变（比如定时任务刚好取消了这单），直接回滚
        // 调用订单Mapper的条件更新方法，将订单状态从0（待确认）改为1（已完成），返回0表示并发冲突
        if (orderMapper.updateStatus(orderId, STATUS_PENDING, STATUS_FINISHED) == 0)
        {
            // 状态更新失败，说明已被其他线程修改，抛出异常触发回滚
            throw new ServiceException("订单状态已变化，请刷新后重试");
        }
        // 记录确认收货时间，更新订单（这里用 updateOrder 而非 mapper 直接改，保持框架风格统一）
        // 将订单对象的状态更新为已完成（与数据库保持一致）
        order.setStatus(STATUS_FINISHED);
        // 设置订单的支付/确认收货时间为当前系统时间
        order.setPayTime(DateUtils.getNowDate());
        // 调用订单Mapper的更新方法，将修改后的订单信息（状态和确认时间）写入数据库
        orderMapper.updateOrder(order);

        // 第2步：买家解除冻结
        // 注意：不是退款！买家的钱已经花了，这里只是把"冻结金额"减掉
        // freezePay 时：余额减少、冻结增加；settleBuyerFrozen 时：冻结减少（钱真正支出）
        // 这样钱包表的 balance + frozen 始终能对上账
        // 调用钱包Service的结算买家冻结方法，将买家的冻结金额清零（钱真正支出，不是退款）
        walletService.settleBuyerFrozen(order.getBuyerId(), order.getPrice(), order.getOrderNo());

        // 第3步：卖家入账
        // 卖家余额增加（金额 = 成交价 - 平台手续费），同时记一条"卖出收入"流水
        // 平台手续费5%在 createOrder 时已经算好了 sellerIncome，这里直接用
        // 调用钱包Service的入账方法，将扣除手续费后的卖家收入添加到卖家余额中
        walletService.settleIncome(order.getSellerId(), order.getSellerIncome(), order.getOrderNo());

        // 第4步：饰品所有权转移
        // 【答辩要点】为什么不直接 UPDATE userId 把卖家改成买家？
        // 因为库存记录还关联了原始获取信息（source 字段），
        // 卖家原来的 source 可能是 "open_box"（开箱）或 "recharge"（充值），
        // 但买家获得这个饰品的来源应该是 "purchase"（购买）。
        // 如果直接改 userId，source 字段就乱了——买家的饰品来源变成了卖家的来源。
        // 所以正确做法是：复制饰品的物理属性（浮动值/磨损/贴纸/检视链接）
        // 创建一条新库存给买家（source=purchase），再删除卖家的旧库存。
        // 这样每件库存的来源信息都是准确的，方便后续追溯。
        // 为什么不直接改 userId？因为库存记录还关联了原始获取信息（source等），
        // 买家的库存应该标记 source=purchase，和卖家原来的 source 不同
        // 所以做法是：复制饰品属性（浮动值/磨损/贴纸/检视链接）创建新库存给买家，再删除卖家旧库存
        // 根据订单关联的库存ID，查询卖家的原始库存记录，获取饰品的物理属性
        Inventory old = inventoryService.selectInventoryById(order.getInventoryId());
        // 判断原始库存记录是否存在，存在才执行饰品转移操作
        if (old != null)
        {
            // 为买家创建新库存，复制饰品的物理属性（这些属性不会因交易改变）
            // 创建一个新的Inventory实体对象，用于给买家创建新的库存记录
            Inventory newInventory = new Inventory();
            // 设置新库存的所属用户为买家，表示饰品所有权转移给了买家
            newInventory.setUserId(order.getBuyerId());   // 新主人：买家
            // 设置新库存关联的饰品ID，与订单中的饰品ID一致
            newInventory.setSkinId(order.getSkinId());     // 同一个饰品
            // 复制原库存的浮动值，这是CS2饰品的唯一物理标识，交易不会改变
            newInventory.setFloatValue(old.getFloatValue()); // 浮动值（CS2饰品唯一标识）
            // 复制原库存的磨损名称（如"略有磨损""崭新出厂"等）
            newInventory.setWearName(old.getWearName());     // 磨损名称（如"略有磨损"）
            // 复制原库存的贴纸信息，记录饰品上贴了哪些贴纸
            newInventory.setStickerInfo(old.getStickerInfo()); // 贴纸信息
            // 复制原库存的检视链接，用于在游戏内查看饰品详情
            newInventory.setInspectLink(old.getInspectLink()); // 检视链接
            // 设置新库存的来源为"purchase"（购买），区别于卖家的原始来源（开箱/充值等）
            newInventory.setSource("purchase");  // 来源标记为"购买"，区别于开箱/充值等
            // 设置新库存状态为0（在库/未上架），买家获得后需要手动上架
            newInventory.setStatus(0);           // 新库存默认在库（未上架）
            // 调用库存Service的添加方法，将买家的新库存记录插入数据库
            inventoryService.addInventory(newInventory);
            // 删除卖家的旧库存（饰品已转移给买家，卖家不再持有）
            // 调用库存Service的删除方法，根据旧库存ID删除卖家的库存记录
            inventoryService.deleteInventoryById(old.getId());
        }
        // 返回1表示确认收货操作成功完成
        return 1;
    }

    /**
     * 取消订单（手动取消 / 定时任务超时取消 共用此方法）
     *
     * <p>取消是下单的逆操作：订单置已取消 -> 买家退款（解冻） -> 库存恢复在售 -> 上架恢复在售。
     * 与 createOrder 一样使用 @Transactional 保证四步操作的原子性，
     * 条件更新防止用户手动取消和定时任务自动取消同时操作同一订单。</p>
     *
     * @param orderId 订单主键
     * @return 1 成功
     */
    // 重写接口方法，执行取消订单操作，回滚下单时的所有操作
    @Override
    // 开启声明式事务，所有异常都触发回滚，保证取消操作四步的原子性
    @Transactional(rollbackFor = Exception.class)
    public int cancelOrder(Long orderId)
    {
        // ==================== 前置校验 ====================
        // 根据订单ID从数据库查询对应的订单记录
        Order order = orderMapper.selectOrderById(orderId);
        // 判断订单是否存在，为空说明传入的订单ID无效
        if (order == null)
        {
            // 抛出业务异常，提示订单不存在，事务回滚
            throw new ServiceException("订单不存在");
        }
        // 只有"待确认"（status=0）的订单才能取消
        // 已完成的订单钱货两清不能取消，已取消的不能重复取消
        // 判断订单当前状态是否为待确认状态，只有待确认的订单才允许取消
        if (order.getStatus() != STATUS_PENDING)
        {
            // 状态不匹配，抛出异常提示不允许取消，事务回滚
            throw new ServiceException("订单状态不允许取消");
        }

        // ==================== 核心四步操作（事务内，与 createOrder 逆序对应） ====================

        // 第1步：订单状态 0→2（条件更新）
        // SQL: UPDATE debuff_order SET status=2 WHERE id=? AND status=0
        // 场景：用户点"取消"的同时定时任务也检测到超时在取消，只有第一个能成功
        // 另一个返回0行走回滚，前端提示"订单状态已变化"
        // 调用订单Mapper的条件更新方法，将订单状态从0（待确认）改为2（已取消），返回0表示并发冲突
        if (orderMapper.updateStatus(orderId, STATUS_PENDING, STATUS_CANCELLED) == 0)
        {
            // 状态更新失败，说明已被其他线程修改（如定时任务同时取消了该订单），抛出异常回滚
            throw new ServiceException("订单状态已变化，请刷新后重试");
        }
        // 记录取消时间
        // 将订单对象的状态更新为已取消（与数据库保持一致）
        order.setStatus(STATUS_CANCELLED);
        // 设置订单的取消时间为当前系统时间，记录取消操作发生的时间点
        order.setCancelTime(DateUtils.getNowDate());
        // 调用订单Mapper的更新方法，将修改后的订单信息（状态和取消时间）写入数据库
        orderMapper.updateOrder(order);

        // 第2步：买家退款（与 confirmOrder 的 settleBuyerFrozen 相反）
        // unfreezePay：冻结金额减少、余额加回（钱退回给买家）
        // 对应 createOrder 中的 freezePay（余额减少、冻结增加），这里是完全逆向操作
        // 调用钱包Service的解冻支付方法，将买家的冻结金额退回可用余额（逆向操作freezePay）
        walletService.unfreezePay(order.getBuyerId(), order.getPrice(), order.getOrderNo());

        // 第3步：库存 2(冻结) -> 1(在售)
        // 下单时库存从1→2（冻结），取消时从2→1（恢复在售），商品重新可以被购买
        // 调用库存Service的条件更新方法，将库存状态从2（冻结）恢复为1（在售），商品重新可被购买
        inventoryService.updateStatus(order.getInventoryId(), 2, 1);

        // 第4步：上架记录 1(已售) -> 0(在售)，清空售出时间
        // 对应 createOrder 中的 markSold（0→1），这里是逆向恢复
        // ListingMapper.updateStatus 的 SQL 会同时把 sold_time 置为 null
        // 调用上架记录Mapper的状态更新方法，将上架状态从1（已售）恢复为0（在售），同时清空售出时间
        listingMapper.updateStatus(order.getListingId(), 1, 0);
        // 返回1表示取消订单操作成功完成
        return 1;
    }

    /**
     * 查询我的交易列表（C端"我的订单"页面）
     *
     * @param userId    当前登录用户ID
     * @param status    订单状态过滤（可选：0待确认 1已完成 2已取消，null查全部）
     * @param direction 方向过滤（可选：1我买的 2我卖的，null查全部即买+卖）
     * @return 订单列表（已按创建时间倒序）
     */
    // 重写接口方法，查询当前用户的订单列表，支持按状态和买卖方向筛选
    @Override
    public List<Order> selectMyOrders(Long userId, Integer status, Integer direction)
    {
        // 调用订单Mapper的查询方法，传入用户ID、状态筛选和方向筛选参数，返回符合条件的订单列表
        return orderMapper.selectMyOrders(userId, status, direction);
    }

    /**
     * 查询超时未确认的待支付订单（供定时任务 OrderTimeoutTask 调用）
     *
     * <p>筛选条件：status=0（待确认）且 create_time 早于"当前时间 - timeoutMinutes 分钟"。
     * 定时任务拿到列表后逐个调用 cancelOrder，每个订单独立 try-catch 防止一个失败影响其他。</p>
     *
     * @param timeoutMinutes 超时阈值（分钟），从配置文件读取
     * @return 超时订单列表
     */
    // 重写接口方法，查询超过指定时间仍未确认的订单列表，供定时任务自动取消使用
    @Override
    public List<Order> selectTimeoutOrders(int timeoutMinutes)
    {
        // 调用订单Mapper的超时查询方法，传入超时阈值（分钟），返回所有超时未确认的订单列表
        return orderMapper.selectTimeoutOrders(timeoutMinutes);
    }

    /**
     * 生成唯一订单号：ORD + yyyyMMddHHmmss + 4位随机数
     *
     * <p>格式示例：ORD202609031430521234
     * 时间戳精确到秒 + 4位随机数（0000-9999），同一秒内万人级并发才可能重复，
     * 对于毕设项目完全够用。生产环境建议用雪花算法或数据库序列。</p>
     *
     * <p>【答辩要点】为什么不用 UUID？</p>
     * <p>① UUID 是36位字符串（如 550e8400-e29b-41d4-a716-446655440000），
     * 太长且无规律，不适合做订单号（用户看到一串随机字符无法判断时间先后）；
     * ② UUID 作为数据库主键会导致 B+ 树索引频繁分裂（随机插入），性能差；
     * ③ 时间戳+随机数的组合既有业务含义（能看出下单时间）又足够唯一，
     * 对于毕设级别的并发量完全够用。生产环境推荐雪花算法（Snowflake），
     * 它生成的是递增的 long 型，兼顾唯一性和有序性。</p>
     */
    // 定义私有方法，生成格式为"ORD+时间戳+4位随机数"的唯一订单编号
    private String generateOrderNo()
    {
        // 取当前时间戳，格式化为 yyyyMMddHHmmss（14位）
        // 使用日期工具类将当前时间格式化为"年月日时分秒"的14位字符串
        String time = DateUtils.parseDateToStr("yyyyMMddHHmmss", DateUtils.getNowDate());
        // 4位随机数，%04d 保证不足4位时前面补0（如 42 → 0042）
        // 生成一个0到9999之间的随机整数，并格式化为4位字符串（不足补零）
        String random = String.format("%04d", new Random().nextInt(10000));
        // 拼接订单号：前缀"ORD" + 14位时间戳 + 4位随机数，共21位字符
        return "ORD" + time + random;
    }
}