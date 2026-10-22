// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.Order;

/**
 * 交易订单Service接口
 * 
 * @author debuff
 * @date 2026-08-25
 */
public interface IOrderService // 订单服务接口
{
    /**
     * 查询交易订单
     * 
     * @param id 交易订单主键
     * @return 交易订单
     */
    public Order selectOrderById(Long id); // 根据ID查询订单

    /**
     * 查询交易订单列表
     * 
     * @param order 交易订单
     * @return 交易订单集合
     */
    public List<Order> selectOrderList(Order order); // 查询订单列表

    /**
     * 新增交易订单
     * 
     * @param order 交易订单
     * @return 结果
     */
    public int insertOrder(Order order); // 新增订单

    /**
     * 修改交易订单
     * 
     * @param order 交易订单
     * @return 结果
     */
    public int updateOrder(Order order); // 修改订单

    /**
     * 批量删除交易订单
     * 
     * @param ids 需要删除的交易订单主键集合
     * @return 结果
     */
    public int deleteOrderByIds(Long[] ids); // 批量删除订单

    /**
     * 删除交易订单信息
     * 
     * @param id 交易订单主键
     * @return 结果
     */
    public int deleteOrderById(Long id); // 删除单条订单

    /**
     * 用户下单购买（冻结买家余额、冻结库存、标记上架已售、生成订单）
     * 
     * @param listingId 上架ID
     * @param buyerId 买家ID
     * @return 生成的订单
     */
    public Order createOrder(Long listingId, Long buyerId); // 创建订单

    /**
     * 买家确认收货（解除冻结、卖家入账、饰品转移给买家）
     * 
     * @param orderId 订单主键
     * @return 结果
     */
    public int confirmOrder(Long orderId); // 确认收货

    /**
     * 取消订单（退款、库存解冻恢复在售、上架恢复在售）
     * 
     * @param orderId 订单主键
     * @return 结果
     */
    public int cancelOrder(Long orderId); // 取消订单

    /**
     * 查询我的交易列表（我买的或我卖的）
     * 
     * @param userId 当前用户ID
     * @param status 订单状态（可选）
     * @param direction 方向（可选：1我买的 2我卖的）
     * @return 订单集合
     */
    public List<Order> selectMyOrders(Long userId, Integer status, Integer direction); // 查询我的订单

    /**
     * 查询超时未确认的待支付订单（用于定时自动取消）
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单集合
     */
    public List<Order> selectTimeoutOrders(int timeoutMinutes); // 查询超时订单
}
