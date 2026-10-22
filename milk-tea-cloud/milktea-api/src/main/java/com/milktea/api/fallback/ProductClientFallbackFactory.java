package com.milktea.api.fallback;

import com.milktea.api.client.ProductClient;
import com.milktea.common.result.Result;
import com.milktea.model.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable cause) {
        if (cause != null) {
            log.error("ProductClient fallback: {}", cause.getMessage());
        }
        return new ProductClient() {
            @Override
            public Result<Product> queryById(Long id) {
                return Result.error("商品服务暂时不可用，请稍后重试");
            }

            @Override
            public Result<java.util.List<Product>> list() {
                return Result.error("商品服务暂时不可用，请稍后重试");
            }
        };
    }
}
