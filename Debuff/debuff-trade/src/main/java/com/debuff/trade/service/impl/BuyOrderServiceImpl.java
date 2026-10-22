// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.trade.domain.Order;
import com.debuff.trade.domain.entity.BuyOrder;
import com.debuff.trade.domain.entity.Inventory;
import com.debuff.trade.domain.entity.Skin;
import com.debuff.trade.domain.vo.BuyOrderVO;
import com.debuff.trade.mapper.BuyOrderMapper;
import com.debuff.trade.mapper.InventoryMapper;
import com.debuff.trade.mapper.OrderMapper;
import com.debuff.trade.mapper.SkinMapper;
import com.debuff.trade.service.IBuyOrderService;
import com.debuff.trade.service.IInventoryService;
import com.debuff.trade.service.IWalletService;

/**
 * 求购单 服务层实现
 *
 * @author B同学
 */
@Service
public class BuyOrderServiceImpl implements IBuyOrderService
{
    /** 平台手续费比例 */
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");

    @Autowired
    private BuyOrderMapper buyOrderMapper; // 求购单Mapper

    @Autowired
    private SkinMapper skinMapper; // 饰品Mapper

    @Autowired
    private IInventoryService inventoryService; // 库存服务

    @Autowired
    private IWalletService walletService; // 钱包服务

    @Autowired
    private OrderMapper orderMapper; // 订单Mapper

    @Autowired
    private InventoryMapper inventoryMapper; // 库存Mapper

    /**
     * 发布求购单
     */
    @Override
    public int createBuyOrder(Long userId, Long skinId, BigDecimal price)
    {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) // 校验价格必须大于0
        {
            throw new ServiceException("求购价格必须大于0");
        }
        Skin skin = skinMapper.selectSkinById(skinId); // 查询饰品是否存在
        if (skin == null)
        {
            throw new ServiceException("饰品不存在");
        }
        if (StringUtils.isNotEmpty(skin.getStatus()) && "1".equals(skin.getStatus())) // 校验饰品是否已下架
        {
            throw new ServiceException("该饰品已下架，无法求购");
        }
        BuyOrder buyOrder = new BuyOrder(); // 创建求购单对象
        buyOrder.setUserId(userId); // 设置求购用户ID
        buyOrder.setSkinId(skinId); // 设置饰品ID
        buyOrder.setPrice(price); // 设置求购价格
        buyOrder.setStatus(Integer.valueOf(0)); // 设置初始状态为有效(0)
        return buyOrderMapper.insertBuyOrder(buyOrder); // 插入求购单
    }

    /**
     * 按饰品查询有效求购
     */
    @Override
    public List<BuyOrderVO> selectBySkin(Long skinId, Integer limit)
    {
        return buyOrderMapper.selectBySkin(skinId, limit); // 调用Mapper查询
    }

    /**
     * 查询我的求购列表
     */
    @Override
    public List<BuyOrderVO> selectMyBuyOrders(Long userId)
    {
        return buyOrderMapper.selectMyBuyOrders(userId); // 调用Mapper查询
    }

    /**
     * 修改求购价格
     */
    @Override
    public int updatePrice(Long id, Long userId, BigDecimal price)
    {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) // 校验价格必须大于0
        {
            throw new ServiceException("求购价格必须大于0");
        }
        BuyOrder buyOrder = buyOrderMapper.selectBuyOrderById(id); // 查询求购单
        if (buyOrder == null || !buyOrder.getUserId().equals(userId)) // 校验求购单归属
        {
            throw new ServiceException("求购单不存在");
        }
        if (!Integer.valueOf(0).equals(buyOrder.getStatus())) // 校验状态必须为有效
        {
            throw new ServiceException("该求购单已失效，无法修改");
        }
        return buyOrderMapper.updatePrice(id, price); // 更新价格
    }

    /**
     * 取消求购单
     */
    @Override
    public int cancelBuyOrder(Long id, Long userId)
    {
        BuyOrder buyOrder = buyOrderMapper.selectBuyOrderById(id); // 查询求购单
        if (buyOrder == null || !buyOrder.getUserId().equals(userId)) // 校验求购单归属
        {
            throw new ServiceException("求购单不存在");
        }
        return buyOrderMapper.cancelBuyOrder(id, userId); // 取消求购单
    }

    /**
     * 卖家接受求购：冻结买家余额 → 冻结卖家库存 → 生成订单 → 求购单置已完成
     * 全部操作在同一事务中，任一步失败整体回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理，异常时回滚
    public Order acceptBuyOrder(Long buyOrderId, Long sellerId, Long inventoryId)
    {
        // 1. 校验求购单存在且有效
        BuyOrder buyOrder = buyOrderMapper.selectBuyOrderById(buyOrderId); // 查询求购单
        if (buyOrder == null)
        {
            throw new ServiceException("求购单不存在");
        }
        if (!Integer.valueOf(0).equals(buyOrder.getStatus())) // 校验求购单状态为有效
        {
            throw new ServiceException("该求购单已失效，无法接受");
        }

        Long buyerId = buyOrder.getUserId(); // 获取买家ID
        Long skinId = buyOrder.getSkinId(); // 获取饰品ID
        BigDecimal price = buyOrder.getPrice(); // 获取求购价格

        // 2. 不能卖给自己
        if (sellerId.equals(buyerId)) // 校验卖家不能是买家本人
        {
            throw new ServiceException("不能接受自己的求购");
        }

        // 3. 校验卖家选择的库存，禁止服务端自动替换成其它饰品实例
        if (inventoryId == null) // 校验库存ID不为空
        {
            throw new ServiceException("请选择要出售的库存");
        }
        Inventory inventory = inventoryMapper.selectInventoryEntityById(inventoryId); // 查询库存实体
        if (inventory == null)
        {
            throw new ServiceException("库存不存在");
        }
        if (!sellerId.equals(inventory.getUserId())) // 校验库存归属当前卖家
        {
            throw new ServiceException("只能出售自己的库存");
        }
        if (!Integer.valueOf(0).equals(inventory.getStatus())) // 校验库存状态为在库(0)
        {
            throw new ServiceException("该库存当前不可出售，请刷新后重试");
        }
        if (!skinId.equals(inventory.getSkinId())) // 校验库存饰品与求购饰品一致
        {
            throw new ServiceException("所选库存与求购饰品不一致");
        }

        // 4. 生成订单编号
        String orderNo = generateOrderNo(); // 生成唯一订单号

        // 5. 计算手续费和卖家实收
        BigDecimal platformFee = price.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP); // 计算平台手续费(5%)
        BigDecimal sellerIncome = price.subtract(platformFee); // 计算卖家实收

        // 6. 冻结买家余额（余额不足时抛异常，整体回滚）
        walletService.freezePay(buyerId, price, orderNo); // 冻结买家余额

        // 7. 冻结卖家库存 0在库 → 2冻结
        if (inventoryService.updateStatus(inventory.getId(), 0, 2) == 0) // 乐观锁更新库存状态
        {
            throw new ServiceException("库存状态已变化，请刷新后重试");
        }

        // 8. 生成交易订单（listingId 为 null，因为来自求购而非上架）
        Order order = new Order(); // 创建订单对象
        order.setOrderNo(orderNo); // 设置订单编号
        order.setSkinId(skinId); // 设置饰品ID
        order.setInventoryId(inventory.getId()); // 设置库存ID
        order.setListingId(null); // 求购交易无上架ID
        order.setBuyerId(buyerId); // 设置买家ID
        order.setSellerId(sellerId); // 设置卖家ID
        order.setPrice(price); // 设置成交价格
        order.setPlatformFee(platformFee); // 设置平台手续费
        order.setSellerIncome(sellerIncome); // 设置卖家实收
        order.setStatus(0); // 设置订单初始状态为待付款(0)
        order.setCreateTime(DateUtils.getNowDate()); // 设置创建时间
        orderMapper.insertOrder(order); // 插入订单

        // 9. 求购单置为已完成
        if (buyOrderMapper.acceptBuyOrder(buyOrderId) == 0) // 更新求购单状态为已完成
        {
            throw new ServiceException("求购单状态已变化，请刷新后重试");
        }

        return order; // 返回生成的订单
    }

    /**
     * 生成订单编号：ORD + yyyyMMddHHmmss + 4位随机数
     */
    private String generateOrderNo()
    {
        String time = DateUtils.parseDateToStr("yyyyMMddHHmmss", DateUtils.getNowDate()); // 格式化当前时间
        String random = String.format("%04d", new Random().nextInt(10000)); // 生成4位随机数
        return "ORD" + time + random; // 拼接订单编号
    }
}
