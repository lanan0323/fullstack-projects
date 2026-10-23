// 包声明
package com.debuff.common.utils;

/**
 * 处理并记录日志文件
 * 
 * @author ruoyi
 */
public class LogUtils
{
    public static String getBlock(Object msg) // 将消息用方括号包裹
    {
        if (msg == null)
        {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}
