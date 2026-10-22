// 包声明
package com.debuff.framework.web.service;

// 导入所需类
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.exception.user.UserPasswordNotMatchException;
import com.debuff.common.exception.user.UserPasswordRetryLimitExceedException;
import com.debuff.common.utils.MessageUtils;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.framework.manager.AsyncManager;
import com.debuff.framework.manager.factory.AsyncFactory;
import com.debuff.framework.security.context.AuthenticationContextHolder;

/**
 * 登录密码方法
 * 
 * @author ruoyi
 */
@Component
public class SysPasswordService
{
    @Autowired
    private RedisCache redisCache; // Redis缓存，存储密码错误次数

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount; // 最大密码错误重试次数

    @Value(value = "${user.password.lockTime}")
    private int lockTime; // 密码错误锁定时间(分钟)

    /**
     * 登录账户密码错误次数缓存键名
     * 
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username)
    {
        return CacheConstants.PWD_ERR_CNT_KEY + username; // 拼接Redis key: pwd_err_cnt:{username}
    }

    public void validate(SysUser user)
    {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext(); // 从ThreadLocal获取认证令牌
        String username = usernamePasswordAuthenticationToken.getName(); // 获取用户名
        String password = usernamePasswordAuthenticationToken.getCredentials().toString(); // 获取明文密码

        Integer retryCount = redisCache.getCacheObject(getCacheKey(username)); // 从Redis获取已重试次数

        if (retryCount == null) // 首次登录，无错误记录
        {
            retryCount = 0;
        }

        if (retryCount >= Integer.valueOf(maxRetryCount).intValue()) // 超过最大重试次数
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.password.retry.limit.exceed", maxRetryCount, lockTime))); // 异步记录超限日志
            throw new UserPasswordRetryLimitExceedException(maxRetryCount, lockTime); // 抛出重试超限异常
        }

        if (!matches(user, password)) // 密码不匹配
        {
            retryCount = retryCount + 1; // 错误次数+1
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL,
                    MessageUtils.message("user.password.retry.limit.count", retryCount))); // 异步记录密码错误日志
            redisCache.setCacheObject(getCacheKey(username), retryCount, lockTime, TimeUnit.MINUTES); // 将错误次数存入Redis，设置锁定过期时间
            throw new UserPasswordNotMatchException(); // 抛出密码不匹配异常
        }
        else // 密码正确
        {
            clearLoginRecordCache(username); // 清除错误次数缓存
        }
    }

    public boolean matches(SysUser user, String rawPassword)
    {
        return SecurityUtils.matchesPassword(rawPassword, user.getPassword()); // BCrypt密码比对
    }

    public void clearLoginRecordCache(String loginName)
    {
        if (redisCache.hasKey(getCacheKey(loginName))) // 缓存key存在
        {
            redisCache.deleteObject(getCacheKey(loginName)); // 删除密码错误次数缓存
        }
    }
}
