package com.milktea.api.fallback;

import com.milktea.api.client.CouponClient;
import com.milktea.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.math.BigDecimal;

@Slf4j
public class CouponClientFallbackFactory implements FallbackFactory<CouponClient> {
    @Override
    public CouponClient create(Throwable cause) {
        if (cause != null) {
            log.error("CouponClient fallback: {}", cause.getMessage());
        }
        return new CouponClient() {
            @Override
            public Result<BigDecimal> use(Long userId, Long couponId, BigDecimal orderAmount) {
                return Result.error("优惠券服务不可用");
            }

            @Override
            public Result<Void> restore(Long userId, Long couponId) {
                return Result.error("优惠券服务不可用");
            }
        };
    }
}
