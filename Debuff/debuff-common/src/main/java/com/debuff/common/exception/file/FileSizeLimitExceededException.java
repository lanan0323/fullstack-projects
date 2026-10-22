// 包声明
package com.debuff.common.exception.file;

/**
 * 文件名大小限制异常类
 * 
 * @author ruoyi
 */
public class FileSizeLimitExceededException extends FileException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public FileSizeLimitExceededException(long defaultMaxSize) // 传入文件大小限制值
    {
        super("upload.exceed.maxSize", new Object[] { defaultMaxSize }); // 错误码和参数
    }
}
