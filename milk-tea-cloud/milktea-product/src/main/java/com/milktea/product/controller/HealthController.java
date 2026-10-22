package com.milktea.product.controller;

import com.milktea.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口: 服务启动后访问 http://localhost:8082/product/health 验证
 */
@RestController
@RequestMapping("/product")
public class HealthController {

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("milktea-product 服务运行正常");
    }
}
