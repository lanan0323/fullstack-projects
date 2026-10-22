package com.milktea.api.fallback;

import com.milktea.api.client.UserClient;
import com.milktea.common.result.Result;
import com.milktea.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        if (cause != null) {
            log.error("UserClient fallback: {}", cause.getMessage());
        }
        return new UserClient() {
            @Override
            public Result<User> queryById(Long id) {
                return Result.error("用户服务不可用");
            }
        };
    }
}