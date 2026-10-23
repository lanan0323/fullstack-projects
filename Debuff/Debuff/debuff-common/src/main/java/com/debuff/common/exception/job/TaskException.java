// 包声明
package com.debuff.common.exception.job;

/**
 * 计划策略异常
 * 
 * @author ruoyi
 */
public class TaskException extends Exception
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    private Code code; // 错误码枚举

    public TaskException(String msg, Code code) // 带消息和错误码的构造方法
    {
        this(msg, code, null);
    }

    public TaskException(String msg, Code code, Exception nestedEx) // 带嵌套异常的构造方法
    {
        super(msg, nestedEx);
        this.code = code;
    }

    public Code getCode() // 获取错误码
    {
        return code;
    }

    public enum Code // 任务异常错误码枚举
    {
        TASK_EXISTS, NO_TASK_EXISTS, TASK_ALREADY_STARTED, UNKNOWN, CONFIG_ERROR, TASK_NODE_NOT_AVAILABLE // 任务已存在/不存在/已启动/未知/配置错误/节点不可用
    }
}
