// 包声明
package com.debuff.common.utils;

// 导入IO和异常处理相关类
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 错误信息处理类。
 *
 * @author ruoyi
 */
public class ExceptionUtil
{
    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e) // 获取异常的完整堆栈信息字符串
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true)); // 将堆栈输出到StringWriter
        return sw.toString();
    }

    public static String getRootErrorMessage(Exception e) // 获取根因异常的消息
    {
        Throwable root = ExceptionUtils.getRootCause(e); // 获取根因异常
        root = (root == null ? e : root); // 无根因则用原异常
        if (root == null)
        {
            return "";
        }
        String msg = root.getMessage(); // 获取根因消息
        if (msg == null)
        {
            return "null";
        }
        return StringUtils.defaultString(msg); // 返回消息(空则返回空串)
    }
}
