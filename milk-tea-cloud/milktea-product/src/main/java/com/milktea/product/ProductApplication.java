package com.milktea.product;

import com.milktea.api.conf.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 商品服务启动类
 * 负责人: 成员B
 */
@SpringBootApplication
@ComponentScan("com.milktea")
@MapperScan("com.milktea.product.mapper")
@EnableFeignClients(basePackages = "com.milktea.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
