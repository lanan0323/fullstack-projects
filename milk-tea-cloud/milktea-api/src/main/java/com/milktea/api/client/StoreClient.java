package com.milktea.api.client;

import com.milktea.api.fallback.StoreClientFallbackFactory;
import com.milktea.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * 店铺服务客户端(库存扣减/配方查询)
 */
@FeignClient(value = "milktea-store", contextId = "storeClient", fallbackFactory = StoreClientFallbackFactory.class)
public interface StoreClient {

    /** 下单扣减原料: items=[{productId, quantity}], 返回扣后预警原料名 */
    @PostMapping("/material/deduct")
    Result<List<String>> deduct(@RequestBody Map<String, Object> request);

    /** 某商品配方 */
    @GetMapping("/recipe/{productId}")
    Result<List<Map<String, Object>>> recipe(@PathVariable("productId") Long productId);
}
