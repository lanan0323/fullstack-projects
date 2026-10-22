package com.milktea.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.milktea.common.utils.JwtUtils;
import com.milktea.gateway.props.CloudProperties;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private CloudProperties cloudProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().value();
        log.info("requestPath={}", requestPath);

        List<String> whitePaths = cloudProperties.getWhitePaths();
        if (whitePaths != null) {
            for (String whitePath : whitePaths) {
                if (antPathMatcher.match(whitePath, requestPath)) {
                    return chain.filter(exchange);
                }
            }
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StrUtil.isBlank(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            Claims claims = JwtUtils.parseJWT("milktea-secret-key-2026", token);
            Object userId = claims.get("userId");
            Object userType = claims.get("userType");
            log.info("userId={}, userType={}", userId, userType);

            exchange.getRequest().mutate()
                    .header("userId", userId.toString())
                    .header("userType", userType != null ? userType.toString() : "0")
                    .build();

            return chain.filter(exchange);
        } catch (Exception e) {
            log.warn("token校验失败: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
