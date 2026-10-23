// 包声明
package com.debuff.common.exception;

/**
 * 业务异常
 * 
 * @author ruoyi
 */
public final class ServiceException extends RuntimeException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /**
     * 错误码
     */
    private Integer code; // HTTP错误码

    /**
     * 错误提示
     */
    private String message; // 错误提示信息

    /**
     * 错误明细，内部调试错误
     *
     * 和 {@link CommonResult#getDetailMessage()} 一致的设计
     */
    private String detailMessage; // 内部调试用的详细错误信息

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException()
    {
    }

    public ServiceException(String message) // 仅消息的构造方法
    {
        this.message = message;
    }

    public ServiceException(String message, Integer code) // 带错误码的构造方法
    {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage() // 获取调试错误明细
    {
        return detailMessage;
    }

    @Override
    public String getMessage() // 获取错误提示
    {
        return message;
    }

    public Integer getCode() // 获取错误码
    {
        return code;
    }

    public ServiceException setMessage(String message) // 设置错误提示,支持链式调用
    {
        this.message = message;
        return this;
    }

    public ServiceException setDetailMessage(String detailMessage) // 设置调试错误明细,支持链式调用
    {
        this.detailMessage = detailMessage;
        return this;
    }
}
