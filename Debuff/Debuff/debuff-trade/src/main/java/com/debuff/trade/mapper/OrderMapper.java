// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.debuff.trade.domain.Order;

/**
 * 交易订单Mapper接口
 * 
 * @author debuff
 * @date 2026-08-25
 */
public interface OrderMapper // 订单Mapper接口
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
     * 删除交易订单
     * 
     * @param id 交易订单主键
     * @return 结果
     */
    public int deleteOrderById(Long id); // 删除单条订单

    /**
     * 批量删除交易订单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrderByIds(Long[] ids); // 批量删除订单

    /**
     * 条件更新订单状态（用于确认/取消的乐观校验）
     * 
     * @param id 订单主键
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @return 受影响行数（0表示原状态不匹配）
     */
    public int updateStatus(@Param("id") Long id, @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus); // 乐观锁更新状态

    /**
     * 查询我的交易列表（我买的或我卖的）
     * 
     * @param userId 当前用户ID
     * @param status 订单状态（可选）
     * @param direction 方向（可选：1我买的 2我卖的）
     * @return 订单集合
     */
    public List<Order> selectMyOrders(@Param("userId") Long userId, @Param("status") Integer status, @Param("direction") Integer direction); // 查询我的订单

    /**
     * 查询超时未确认的待支付订单（用于定时自动取消）
     * 
     * @param timeoutMinutes 超时分钟数（创建时间早于当前时间-该值即视为超时）
     * @return 超时订单集合
     */
    public List<Order> selectTimeoutOrders(@Param("timeoutMinutes") int timeoutMinutes); // 查询超时订单
}
