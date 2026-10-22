package com.milktea.api.fallback;

import com.milktea.api.client.OrderStatsClient;
import com.milktea.common.result.Result;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单统计客户端降级: 返回零值, 保证工作台不至于报错
 */
@Component
public class OrderStatsClientFallbackFactory implements org.springframework.cloud.openfeign.FallbackFactory<OrderStatsClient> {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(OrderStatsClientFallbackFactory.class);

    @Override
    public OrderStatsClient create(Throwable cause) {
        log.warn("订单统计服务不可用, 已降级为空数据: {}", cause.getMessage());
        return new OrderStatsClient() {
            @Override
            public Result<Map<String, Object>> stats(Integer days, Integer rankLimit) {
                Map<String, Object> zero = new HashMap<>();
                zero.put("todayOrderCount", 0);
                zero.put("todayRevenue", BigDecimal.ZERO);
                zero.put("trend", java.util.Collections.emptyList());
                zero.put("rank", java.util.Collections.emptyList());
                return Result.success(zero);
            }
        };
    }
}
