package com.milktea.api.client;

import com.milktea.api.fallback.OrderStatsClientFallbackFactory;
import com.milktea.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 订单统计客户端(store服务调用order服务)
 */
@FeignClient(value = "milktea-order", contextId = "orderStatsClient", fallbackFactory = OrderStatsClientFallbackFactory.class)
public interface OrderStatsClient {

    /** 今日概况 + 近N日营业额趋势 + 商品销量排行 */
    @GetMapping("/order/stats/internal")
    Result<Map<String, Object>> stats(@RequestParam("days") Integer days,
                                      @RequestParam("rankLimit") Integer rankLimit);
}
