package com.milktea.api.fallback;

import com.milktea.api.client.StoreClient;
import com.milktea.common.result.Result;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 店铺服务客户端降级: 返回error, 由调用方决定拦截或降级
 */
@Component
public class StoreClientFallbackFactory implements org.springframework.cloud.openfeign.FallbackFactory<StoreClient> {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(StoreClientFallbackFactory.class);

    @Override
    public StoreClient create(Throwable cause) {
        log.warn("店铺服务不可用: {}", cause.getMessage());
        return new StoreClient() {
            @Override
            public Result<List<String>> deduct(Map<String, Object> request) {
                return Result.error("库存服务不可用");
            }

            @Override
            public Result<List<Map<String, Object>>> recipe(Long productId) {
                return Result.error("库存服务不可用");
            }
        };
    }
}
