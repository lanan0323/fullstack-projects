package com.milktea.api.fallback;

import com.milktea.api.client.PointsClient;
import com.milktea.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.math.BigDecimal;

@Slf4j
public class PointsClientFallbackFactory implements FallbackFactory<PointsClient> {
    @Override
    public PointsClient create(Throwable cause) {
        if (cause != null) {
            log.error("PointsClient fallback: {}", cause.getMessage());
        }
        return new PointsClient() {
            @Override
            public Result<BigDecimal> deduct(Long userId, long points, BigDecimal orderAmount, String orderNo) {
                return Result.error("积分服务不可用");
            }

            @Override
            public Result<BigDecimal> getDiscount(Long userId) {
                return Result.error("积分服务不可用");
            }
        };
    }
}
