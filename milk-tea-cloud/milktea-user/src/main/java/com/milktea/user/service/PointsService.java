package com.milktea.user.service;

import com.milktea.model.entity.PointsFlow;

import java.util.List;
import java.util.Map;

public interface PointsService {
    /** 我的积分(首次自动建档) */
    Map<String, Object> myPoints();
    /** 加积分并记流水(注册礼包/充值赠送等) */
    void earn(Long userId, long points, String remark);
    /** 积分明细 */
    List<PointsFlow> flows(int limit);
    /** 结算已完成订单的消费积分(1元=1分, 惰性触发, 幂等), 返回本次新加积分 */
    int settleCompletedOrders(Long userId);
    /** 下单积分抵现(100分=1元, 按需扣减且封顶到应付金额), 返回实际抵扣金额 */
    java.math.BigDecimal deductForOrder(Long userId, long points, java.math.BigDecimal orderAmount, String orderNo);
    /** 等级折扣: 白银0.98/黄金0.95/钻石0.90, 其余1.00 (下单链路, 不触发结算) */
    java.math.BigDecimal getDiscount(Long userId);
}
