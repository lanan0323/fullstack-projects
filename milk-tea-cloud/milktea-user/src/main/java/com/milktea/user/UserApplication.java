package com.milktea.user;

import com.milktea.api.conf.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务启动类
 * 负责人: 成员A
 */
@SpringBootApplication
@ComponentScan("com.milktea")
@MapperScan("com.milktea.user.mapper")
@EnableFeignClients(basePackages = "com.milktea.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
