// 包声明
package com.debuff.common.exception.file;

/**
 * 文件名称超长限制异常类
 * 
 * @author ruoyi
 */
public class FileNameLengthLimitExceededException extends FileException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public FileNameLengthLimitExceededException(int defaultFileNameLength) // 传入文件名长度限制值
    {
        super("upload.filename.exceed.length", new Object[] { defaultFileNameLength }); // 错误码和参数
    }
}
