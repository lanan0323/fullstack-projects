package com.milktea.api.client;

import com.milktea.api.fallback.CouponClientFallbackFactory;
import com.milktea.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 优惠券客户端(调用用户服务)
 * 注: 与 UserClient 同指向 milktea-user, 需用 contextId 区分避免 Bean 冲突
 */
@FeignClient(value = "milktea-user", contextId = "couponClient", fallbackFactory = CouponClientFallbackFactory.class)
public interface CouponClient {

    /** 下单核销优惠券, 返回抵扣金额 */
    @PostMapping("/user/coupon/use")
    Result<BigDecimal> use(@RequestParam("userId") Long userId,
                           @RequestParam("couponId") Long couponId,
                           @RequestParam("orderAmount") BigDecimal orderAmount);

    /** 取消订单回退优惠券 */
    @PostMapping("/user/coupon/restore/{couponId}")
    Result<Void> restore(@RequestParam("userId") Long userId, @PathVariable("couponId") Long couponId);
}
