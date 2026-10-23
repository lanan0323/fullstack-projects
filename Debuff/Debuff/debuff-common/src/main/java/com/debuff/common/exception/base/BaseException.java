// 包声明
package com.debuff.common.exception.base;

// 导入消息工具类和字符串工具类
import com.debuff.common.utils.MessageUtils;
import com.debuff.common.utils.StringUtils;

/**
 * 基础异常
 * 
 * @author ruoyi
 */
public class BaseException extends RuntimeException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /**
     * 所属模块
     */
    private String module; // 异常所属的模块名

    /**
     * 错误码
     */
    private String code; // 错误消息编码

    /**
     * 错误码对应的参数
     */
    private Object[] args; // 消息格式化参数

    /**
     * 错误消息
     */
    private String defaultMessage; // 默认错误消息

    public BaseException(String module, String code, Object[] args, String defaultMessage) // 完整参数构造方法
    {
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    public BaseException(String module, String code, Object[] args) // 无默认消息的构造方法
    {
        this(module, code, args, null);
    }

    public BaseException(String module, String defaultMessage) // 仅模块和默认消息的构造方法
    {
        this(module, null, null, defaultMessage);
    }

    public BaseException(String code, Object[] args) // 仅错误码和参数的构造方法
    {
        this(null, code, args, null);
    }

    public BaseException(String defaultMessage) // 仅默认消息的构造方法
    {
        this(null, null, null, defaultMessage);
    }

    @Override
    public String getMessage() // 获取错误消息,优先用错误码查国际化消息
    {
        String message = null;
        if (!StringUtils.isEmpty(code)) // 有错误码时查国际化消息
        {
            message = MessageUtils.message(code, args);
        }
        if (message == null) // 查不到则用默认消息
        {
            message = defaultMessage;
        }
        return message;
    }

    public String getModule() // 获取所属模块
    {
        return module;
    }

    public String getCode() // 获取错误码
    {
        return code;
    }

    public Object[] getArgs() // 获取消息参数
    {
        return args;
    }

    public String getDefaultMessage() // 获取默认消息
    {
        return defaultMessage;
    }
}
