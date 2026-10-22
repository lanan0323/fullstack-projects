package com.milktea.store;

import com.milktea.api.conf.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 门店服务启动类
 * 负责人: 成员D
 */
@SpringBootApplication
@ComponentScan("com.milktea")
@MapperScan("com.milktea.store.mapper")
@EnableFeignClients(basePackages = "com.milktea.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class StoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
    }
}
