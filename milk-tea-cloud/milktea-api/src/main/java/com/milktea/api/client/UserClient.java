package com.milktea.api.client;

import com.milktea.api.fallback.UserClientFallbackFactory;
import com.milktea.common.result.Result;
import com.milktea.model.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "milktea-user", fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {
    @GetMapping("/user/{id}")
    Result<User> queryById(@PathVariable("id") Long id);
}