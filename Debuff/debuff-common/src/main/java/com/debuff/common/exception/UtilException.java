// 包声明
package com.debuff.common.exception;

/**
 * 工具类异常
 * 
 * @author ruoyi
 */
public class UtilException extends RuntimeException
{
    private static final long serialVersionUID = 8247610319171014183L; // 序列化版本号

    public UtilException(Throwable e) // 包装其他异常的构造方法
    {
        super(e.getMessage(), e);
    }

    public UtilException(String message) // 仅消息的构造方法
    {
        super(message);
    }

    public UtilException(String message, Throwable throwable) // 带消息和原因的构造方法
    {
        super(message, throwable);
    }
}
