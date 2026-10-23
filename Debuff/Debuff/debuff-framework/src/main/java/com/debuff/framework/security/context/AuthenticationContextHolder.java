// 包声明
package com.debuff.framework.security.context;

// 导入所需类
import org.springframework.security.core.Authentication;

/**
 * 身份验证信息
 * 
 * @author ruoyi
 */
public class AuthenticationContextHolder
{
    private static final ThreadLocal<Authentication> contextHolder = new ThreadLocal<>(); // 线程本地变量，存储当前线程的认证信息

    public static Authentication getContext() // 获取当前线程的认证信息
    {
        return contextHolder.get();
    }

    public static void setContext(Authentication context) // 设置当前线程的认证信息
    {
        contextHolder.set(context);
    }

    public static void clearContext() // 清除当前线程的认证信息，防止内存泄漏
    {
        contextHolder.remove();
    }
}
