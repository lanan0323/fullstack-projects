// 声明当前类所在的包路径：上架服务的实现层
package com.debuff.trade.service.impl;

// 导入BigDecimal类型，用于精确处理价格/金额相关的数值运算和比较
import java.math.BigDecimal;
// 导入Spring的自动注入注解，用于依赖注入Mapper等组件
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring的Service注解，标记当前类为业务层组件并注册到Spring容器
import org.springframework.stereotype.Service;
// 导入Spring的事务注解，用于声明方法的事务边界
import org.springframework.transaction.annotation.Transactional;
// 导入自定义的业务异常类，用于抛出业务逻辑错误（如价格非法、库存不存在等）
import com.debuff.common.exception.ServiceException;
// 导入库存实体类，代表用户的一件饰品库存记录
import com.debuff.trade.domain.entity.Inventory;
// 导入上架领域对象，代表市场上的一条在售记录
import com.debuff.trade.domain.Listing;
// 导入饰品实体类，代表饰品的公共信息（如参考价min_price）
import com.debuff.trade.domain.entity.Skin;
// 导入库存Mapper，用于操作库存表的数据库交互层
import com.debuff.trade.mapper.InventoryMapper;
// 导入上架Mapper，用于操作上架记录表的数据库交互层
import com.debuff.trade.mapper.ListingMapper;
// 导入饰品Mapper，用于操作饰品表的数据库交互层
import com.debuff.trade.mapper.SkinMapper;
// 导入上架服务接口，当前实现类需要实现该接口定义的上架/下架方法
import com.debuff.trade.service.IListingService;

/**
 * 在售饰品Service业务层处理（用户端上架/下架）
 *
 * <p>上架模块是交易链路的起点：用户从库存中选择饰品设置价格挂到市场，
 * 其他用户浏览市场时看到的就是这里的上架记录。上架本质是"库存状态变更 + 创建售卖记录"。</p>
 *
 * <h3>核心数据关系</h3>
 * <pre>
 *   debuff_inventory（库存）  ←→  debuff_skin_listing（上架记录）
 *   一件库存对应一条在售记录（每个库存最多一条在售 listing）
 *   库存状态：0在库 → 1在售 → 0在库（下架回退）
 *   上架状态：0在售 → 1已售（被购买）/ 2已下架（主动下架）
 * </pre>
 *
 * <h3>与交易模块的协作</h3>
 * <ul>
 *   <li>下单时 OrderServiceImpl 会调用 listingMapper.markSold 将上架状态改为已售</li>
 *   <li>取消订单时会调用 listingMapper.updateStatus 将上架状态恢复为在售</li>
 * </ul>
 *
 * <h3>【答辩要点】这里 @Transactional 没写 rollbackFor，和 OrderServiceImpl 不一样，有什么区别？</h3>
 * <p>@Transactional 默认 rollbackFor 只回滚 RuntimeException。本模块中抛出的都是
 * ServiceException（继承自 RuntimeException），所以不加 rollbackFor 也能正确回滚。
 * OrderServiceImpl 加了是因为它调用了更多外部 Service，防御性更强。
 * 严格来说两个模块都加上 rollbackFor = Exception.class 是最佳实践，
 * 这里没加是因为当前代码路径中不存在 checked exception 的风险。</p>
 *
 * <h3>【答辩要点】上架涉及库存表和上架表两张表，怎么保证数据一致性？</h3>
 * <p>靠 @Transactional 事务：创建上架记录 + 更新库存状态 + 同步饰品参考价 这三步
 * 在同一个事务中，任一步失败全部回滚。比如第2步更新库存状态失败（并发冲突），
 * 第1步创建的上架记录也会被回滚，不会出现"库存没上架但市场多了一条记录"的不一致。</p>
 *
 * <h3>【答辩要点】为什么不通过数据库唯一约束防重复上架，而是用代码查询判断？</h3>
 * <p>理论上可以在 (inventory_id, status) 上加唯一索引，但：
 * ① 唯一约束抛出的是 DataIntegrityViolationException，错误信息不友好；
 * ② 代码层判断可以先查询再给出明确的业务提示（"该库存已在售"），用户体验更好；
 * ③ 条件更新（WHERE status=0）已经兜底了并发场景，即使两个请求同时通过了校验，
 * 第二个请求的 updateStatus 会返回0行走回滚。所以代码校验 + 条件更新双重保障。</p>
 *
 * @author debuff
 */
// 标记为Service组件，Spring容器会自动扫描并注册为Bean，实现上架/下架的业务逻辑
@Service
// 定义上架服务实现类，实现IListingService接口，提供上架和下架的具体业务逻辑
public class ListingServiceImpl implements IListingService
{
    // 定义库存状态常量：0表示在库（未上架），饰品在用户库存中
    /** 库存状态：在库（未上架） */
    private static final int INV_STATUS_IN = 0;

    // 定义库存状态常量：1表示在售（已上架），饰品挂在市场上等待买家购买
    /** 库存状态：在售（已上架，等待买家购买） */
    private static final int INV_STATUS_ON = 1;

    // 定义上架价格上限常量：100万元，防止用户恶意设置天价挂单
    /** 上架价格上限：100万元（防止恶意天价挂单） */
    private static final BigDecimal MAX_PRICE = new BigDecimal("1000000");

    // 自动注入上架记录Mapper，用于对上架记录表进行增删改查操作
    @Autowired
    private ListingMapper listingMapper;

    // 自动注入库存Mapper，用于查询和更新库存表中的库存状态
    @Autowired
    private InventoryMapper inventoryMapper;

    // 自动注入饰品Mapper，用于更新饰品表中的参考价格字段
    @Autowired
    private SkinMapper skinMapper;

    /**
     * 上架：将库存中的饰品挂到市场出售
     *
     * <p>操作流程：校验 → 创建上架记录 → 更新库存状态 → 同步参考价到饰品表</p>
     * <p>事务保证：三步操作在同一个 @Transactional 中，任一步失败全部回滚。</p>
     *
     * @param userId      当前登录用户ID（卖家）
     * @param inventoryId 要上架的库存ID
     * @param price       卖家设定的售价
     * @return 创建的上架记录
     */
    // 重写IListingService接口中定义的上架方法
    @Override
    // 开启事务，保证创建上架记录、更新库存状态、同步参考价三步操作的原子性
    @Transactional
    // 方法签名：接收用户ID、库存ID和价格三个参数，返回创建的上架记录对象
    public Listing createListing(Long userId, Long inventoryId, BigDecimal price)
    {
        // ==================== 前置校验阶段 ====================


        // 校验1：价格合法性
        // 价格必须 > 0（不能免费赠送）且 <= 100万（防止恶意天价）
        // 判断价格是否为空或小于等于0，不合法则抛出异常
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
        {
            // 价格不合法时抛出业务异常，提示用户价格必须大于0
            throw new ServiceException("价格必须大于0");
        }
        // 判断价格是否超过上限100万元，防止恶意天价挂单
        if (price.compareTo(MAX_PRICE) > 0)
        {
            // 价格超限时抛出业务异常，提示用户价格不能超过100万元
            throw new ServiceException("价格不能超过100万元");
        }

        // 校验2：库存存在性
        // 根据库存ID查询库存实体，校验该库存是否真实存在
        Inventory inventory = inventoryMapper.selectInventoryEntityById(inventoryId);
        // 如果查询结果为null，说明该库存ID在数据库中不存在
        if (inventory == null)
        {
            // 库存不存在时抛出业务异常
            throw new ServiceException("库存不存在");
        }

        // 校验3：库存归属 —— 只能上架自己的库存（防止操作他人饰品）
        // 比较库存记录中的用户ID与当前登录用户ID是否一致，不一致则拒绝操作
        if (!inventory.getUserId().equals(userId))
        {
            // 归属校验失败时抛出业务异常，防止用户操作他人的库存
            throw new ServiceException("只能上架自己的库存");
        }

        // 校验4：库存状态 —— 只有在库（status=0）的才能上架
        // 已经在售的（status=1）或冻结中的（status=2）不能重复上架
        // 判断库存状态是否为空或不等于在库状态（0），不满足条件则拒绝上架
        if (inventory.getStatus() == null || inventory.getStatus() != INV_STATUS_IN)
        {
            // 库存状态不满足上架条件时抛出业务异常
            throw new ServiceException("当前状态不可上架");
        }

        // 校验5：防重复上架 —— 查询该库存是否已有在售的上架记录
        // selectListingByInventoryId 的 SQL 带有 WHERE status=0，只查在售的
        // 如果有在售记录说明已经上架过了，不能重复挂单
        // 查询该库存是否已存在在售状态的上架记录，防止重复上架
        if (listingMapper.selectListingByInventoryId(inventoryId) != null)
        {
            // 已存在在售记录时抛出业务异常，提示该库存已在售
            throw new ServiceException("该库存已在售");
        }

        // ==================== 核心三步操作（事务内） ====================

        // 第1步：创建上架记录
        // 记录卖家ID、饰品ID、售价，状态默认为0（在售），上架时间为当前时间
        // 创建一个新的上架记录对象，用于封装即将写入数据库的上架数据
        Listing listing = new Listing();
        // 设置上架记录关联的库存ID，建立与库存表的关联关系
        listing.setInventoryId(inventoryId);
        // 设置卖家ID为当前登录用户，标记这件商品是谁挂出来的
        listing.setSellerId(userId);
        // 从库存中取饰品ID，保持数据一致性
        listing.setSkinId(inventory.getSkinId()); // 从库存中取饰品ID，保持数据一致性
        // 设置卖家设定的售价，即买家需要支付的金额
        listing.setPrice(price);
        // 将上架记录插入数据库，生成主键ID并持久化
        listingMapper.insertListing(listing);

        // 第2步：更新库存状态 0(在库) → 1(在售)
        // 使用条件更新：WHERE id=? AND status=0，防止并发场景下库存状态已被改变
        // 返回0行说明状态已变化（比如刚好被其他操作改了），抛异常回滚
        // 执行条件更新，将库存状态从在库(0)改为在售(1)，返回受影响的行数
        int rows = inventoryMapper.updateStatus(inventoryId, INV_STATUS_IN, INV_STATUS_ON);
        // 如果返回0行，说明库存状态已被其他操作改变（并发冲突）
        if (rows == 0)
        {
            // 并发冲突时抛出异常，事务会自动回滚之前创建的上架记录
            throw new ServiceException("上架失败：库存状态已变化");
        }

        // 第3步：同步最新上架价到饰品表的 min_price 字段
        // 管理端饰品列表/编辑页面会显示这个参考价，方便管理员了解市场行情
        // 注意：这里不是精确的最低价，而是最后一次上架的价格（简化实现）
        // 创建饰品对象，用于更新饰品表中的参考价格字段
        Skin skin = new Skin();
        // 设置饰品ID，从库存记录中获取对应的饰品ID
        skin.setId(inventory.getSkinId());
        // 将本次上架价格设置为饰品的参考最低价（简化实现，实际应为市场最低价）
        skin.setMinPrice(price);
        // 调用Mapper将参考价格更新到饰品表的min_price字段
        skinMapper.updateMinPrice(skin);

        // 返回成功创建的上架记录对象，包含数据库生成的ID等信息
        return listing;
    }

    /**
     * 下架：将市场在售的饰品撤回库存
     *
     * <p>上架的逆操作：上架记录状态 0(在售)→2(已下架)，库存状态 1(在售)→0(在库)。
     * 下架后饰品回到用户的库存中，可以重新上架或保留在库。</p>
     *
     * @param userId      当前登录用户ID（必须是卖家本人）
     * @param inventoryId 要下架的库存ID
     */
    // 重写IListingService接口中定义的下架方法
    @Override
    // 开启事务，保证下架记录和库存状态回退两步操作的原子性
    @Transactional
    // 方法签名：接收用户ID和库存ID两个参数，无返回值
    public void offShelf(Long userId, Long inventoryId)
    {
        // 查询该库存在售的上架记录（status=0 的才是在售）
        // 根据库存ID查询是否存在在售状态的上架记录
        Listing listing = listingMapper.selectListingByInventoryId(inventoryId);
        // 如果查询结果为null，说明该库存没有在售的上架记录
        if (listing == null)
        {
            // 没有在售记录时抛出业务异常，提示用户无法下架
            throw new ServiceException("该库存没有在售记录");
        }
        // 权限校验：只能下架自己的上架（防止恶意下架他人商品）
        // 比较上架记录中的卖家ID与当前登录用户ID是否一致
        if (!listing.getSellerId().equals(userId))
        {
            // 权限校验失败时抛出业务异常，防止用户下架他人的商品
            throw new ServiceException("只能下架自己的上架");
        }

        // 第1步：上架记录状态 0(在售) → 2(已下架)
        // 条件更新：WHERE id=? AND status=0，防止已被购买（status=1）还能下架
        // 如果商品刚好被买走（status已变为1），这里返回0行走回滚，提示"状态已变化"
        // 执行条件更新，将上架记录状态从在售(0)改为已下架(2)，返回受影响行数
        int rows = listingMapper.updateStatus(listing.getId(), 0, 2);
        // 如果返回0行，说明上架记录状态已被改变（可能刚好被买家购买）
        if (rows == 0)
        {
            // 状态已变化时抛出异常，事务回滚，防止重复下架或误下架已售商品
            throw new ServiceException("下架失败：该记录状态已变化");
        }

        // 第2步：库存状态 1(在售) → 0(在库)，饰品回到用户库存
        // 将库存状态从在售(1)恢复为在库(0)，饰品重新回到用户的库存中
        inventoryMapper.updateStatus(inventoryId, INV_STATUS_ON, INV_STATUS_IN);
    }
}
