package com.milktea.api.conf;

import com.milktea.api.fallback.CouponClientFallbackFactory;
import com.milktea.api.fallback.PointsClientFallbackFactory;
import com.milktea.api.fallback.ProductClientFallbackFactory;
import com.milktea.api.fallback.UserClientFallbackFactory;
import com.milktea.api.interceptor.UserInfoInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new UserInfoInterceptor();
    }

    @Bean
    public UserClientFallbackFactory userClientFallbackFactory() {
        return new UserClientFallbackFactory();
    }

    @Bean
    public ProductClientFallbackFactory productClientFallbackFactory() {
        return new ProductClientFallbackFactory();
    }

    @Bean
    public CouponClientFallbackFactory couponClientFallbackFactory() {
        return new CouponClientFallbackFactory();
    }

    @Bean
    public PointsClientFallbackFactory pointsClientFallbackFactory() {
        return new PointsClientFallbackFactory();
    }
}
