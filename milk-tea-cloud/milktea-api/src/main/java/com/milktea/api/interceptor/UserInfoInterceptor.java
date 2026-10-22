package com.milktea.api.interceptor;

import com.milktea.common.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class UserInfoInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null) {
            template.header("userId", currentUserId.toString());
        }
    }
}
