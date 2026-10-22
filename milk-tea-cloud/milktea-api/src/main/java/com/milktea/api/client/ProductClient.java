package com.milktea.api.client;

import com.milktea.api.fallback.ProductClientFallbackFactory;
import com.milktea.common.result.Result;
import com.milktea.model.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "milktea-product", fallbackFactory = ProductClientFallbackFactory.class)
public interface ProductClient {

    @GetMapping("/product/{id}")
    Result<Product> queryById(@PathVariable("id") Long id);

    /** 全量商品列表(在售商品数统计用) */
    @GetMapping("/product/list")
    Result<java.util.List<Product>> list();
}
