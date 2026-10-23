// 包声明
package com.debuff.framework.security.context;

// 导入所需类
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import com.debuff.common.core.text.Convert;

/**
 * 权限信息
 * 
 * @author ruoyi
 */
public class PermissionContextHolder
{
    private static final String PERMISSION_CONTEXT_ATTRIBUTES = "PERMISSION_CONTEXT"; // 权限上下文属性key

    public static void setContext(String permission) // 将权限标识存入请求属性中
    {
        RequestContextHolder.currentRequestAttributes().setAttribute(PERMISSION_CONTEXT_ATTRIBUTES, permission, // 设置到当前请求属性
                RequestAttributes.SCOPE_REQUEST);
    }

    public static String getContext() // 从请求属性中获取权限标识
    {
        return Convert.toStr(RequestContextHolder.currentRequestAttributes().getAttribute(PERMISSION_CONTEXT_ATTRIBUTES, // 从当前请求属性中获取
                RequestAttributes.SCOPE_REQUEST));
    }
}
