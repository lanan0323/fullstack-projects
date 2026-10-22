// 包声明
package com.debuff.common.exception.file;

// 导入打印流和打印写入器
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 文件上传异常类
 * 
 * @author ruoyi
 */
public class FileUploadException extends Exception
{

    private static final long serialVersionUID = 1L; // 序列化版本号

    private final Throwable cause; // 异常原因

    public FileUploadException() // 空构造方法
    {
        this(null, null);
    }

    public FileUploadException(final String msg) // 仅消息的构造方法
    {
        this(msg, null);
    }

    public FileUploadException(String msg, Throwable cause) // 带消息和原因的构造方法
    {
        super(msg);
        this.cause = cause;
    }

    @Override
    public void printStackTrace(PrintStream stream) // 输出堆栈到PrintStream,包含cause信息
    {
        super.printStackTrace(stream);
        if (cause != null) // 有原因异常时追加输出
        {
            stream.println("Caused by:");
            cause.printStackTrace(stream);
        }
    }

    @Override
    public void printStackTrace(PrintWriter writer) // 输出堆栈到PrintWriter,包含cause信息
    {
        super.printStackTrace(writer);
        if (cause != null) // 有原因异常时追加输出
        {
            writer.println("Caused by:");
            cause.printStackTrace(writer);
        }
    }

    @Override
    public Throwable getCause() // 获取异常原因
    {
        return cause;
    }
}
