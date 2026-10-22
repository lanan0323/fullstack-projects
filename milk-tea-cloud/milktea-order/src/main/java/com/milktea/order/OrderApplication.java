package com.milktea.order;

import com.milktea.api.conf.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 订单服务启动类
 * 负责人: 成员C
 */
@SpringBootApplication
@ComponentScan("com.milktea")
@MapperScan("com.milktea.order.mapper")
@EnableFeignClients(basePackages = "com.milktea.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
