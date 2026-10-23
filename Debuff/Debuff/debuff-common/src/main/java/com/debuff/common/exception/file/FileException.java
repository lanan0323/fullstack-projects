// 包声明
package com.debuff.common.exception.file;

// 导入基础异常类
import com.debuff.common.exception.base.BaseException;

/**
 * 文件信息异常类
 * 
 * @author ruoyi
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public FileException(String code, Object[] args) // 文件异常构造方法,模块固定为file
    {
        super("file", code, args, null);
    }

}
