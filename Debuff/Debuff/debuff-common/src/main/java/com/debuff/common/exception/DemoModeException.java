// 包声明
package com.debuff.common.exception;

/**
 * 演示模式异常
 * 
 * @author ruoyi
 */
public class DemoModeException extends RuntimeException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public DemoModeException() // 演示模式下不允许操作
    {
    }
}
