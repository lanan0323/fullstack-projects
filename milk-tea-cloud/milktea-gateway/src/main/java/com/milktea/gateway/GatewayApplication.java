package com.milktea.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 网关基于 WebFlux/Netty，自身不连接数据库；
// 但 common 经传递依赖（或 IDE 把其它带 mybatis 的模块打进运行配置）会把 JDBC 连接池带进 classpath，
// 触发 Spring Boot 的 DataSourceAutoConfiguration 报错。网关不需要数据源，直接禁用该自动配置。
@SpringBootApplication(
        scanBasePackages = "com.milktea.gateway",
        exclude = {DataSourceAutoConfiguration.class})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
