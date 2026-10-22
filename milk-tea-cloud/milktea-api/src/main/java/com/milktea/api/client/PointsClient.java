package com.milktea.api.client;

import com.milktea.api.fallback.PointsClientFallbackFactory;
import com.milktea.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 积分客户端(调用用户服务)
 * 注: 与 UserClient/CouponClient 同指向 milktea-user, 需用 contextId 区分避免 Bean 冲突
 */
@FeignClient(value = "milktea-user", contextId = "pointsClient", fallbackFactory = PointsClientFallbackFactory.class)
public interface PointsClient {

    /** 下单积分抵现: 按需扣减并封顶到应付金额, 返回实际抵扣金额 */
    @PostMapping("/user/points/deduct")
    Result<BigDecimal> deduct(@RequestParam("userId") Long userId,
                              @RequestParam("points") long points,
                              @RequestParam("orderAmount") BigDecimal orderAmount,
                              @RequestParam(value = "orderNo", required = false) String orderNo);

    /** 等级折扣: 白银0.98/黄金0.95/钻石0.90, 其余1.00 */
    @GetMapping("/user/points/discount")
    Result<BigDecimal> getDiscount(@RequestParam("userId") Long userId);
}
