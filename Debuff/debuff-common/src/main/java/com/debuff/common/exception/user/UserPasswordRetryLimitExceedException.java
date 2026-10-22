// 包声明
package com.debuff.common.exception.user;

/**
 * 用户错误最大次数异常类
 * 
 * @author ruoyi
 */
public class UserPasswordRetryLimitExceedException extends UserException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public UserPasswordRetryLimitExceedException(int retryLimitCount, int lockTime) // 传入重试次数限制和锁定时间
    {
        super("user.password.retry.limit.exceed", new Object[] { retryLimitCount, lockTime }); // 错误码和参数
    }
}
