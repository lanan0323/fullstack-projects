package com.milktea.user.controller;

import com.milktea.common.result.Result;
import com.milktea.model.entity.PointsFlow;
import com.milktea.user.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员积分: 查询/明细(挂/user/points, 复用网关/api/user/**路由与登录鉴权)
 * 积分获取: 注册礼包100分, 钱包充值1元=1积分(下单积分待订单服务对接)
 */
@RestController
@RequestMapping("/user/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping
    public Result<Map<String, Object>> myPoints() {
        return Result.success(pointsService.myPoints());
    }

    @GetMapping("/flows")
    public Result<List<PointsFlow>> flows(@RequestParam(defaultValue = "50") int limit) {
        return Result.success(pointsService.flows(limit));
    }

    /**
     * 下单积分抵现(订单服务Feign内部调用, 与/user/coupon/use同模式)
     * 按需扣减并封顶到应付金额, 返回实际抵扣金额
     */
    @PostMapping("/deduct")
    public Result<java.math.BigDecimal> deduct(@RequestParam Long userId,
                                               @RequestParam long points,
                                               @RequestParam java.math.BigDecimal orderAmount,
                                               @RequestParam(required = false) String orderNo) {
        return Result.success(pointsService.deductForOrder(userId, points, orderAmount, orderNo));
    }

    /** 等级折扣(订单服务Feign内部调用): 白银0.98/黄金0.95/钻石0.90 */
    @GetMapping("/discount")
    public Result<java.math.BigDecimal> discount(@RequestParam Long userId) {
        return Result.success(pointsService.getDiscount(userId));
    }
}
