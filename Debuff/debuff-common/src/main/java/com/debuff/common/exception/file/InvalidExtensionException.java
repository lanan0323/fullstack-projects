// 包声明
package com.debuff.common.exception.file;

// 导入Arrays工具类用于格式化数组输出
import java.util.Arrays;

/**
 * 文件上传 误异常类
 * 
 * @author ruoyi
 */
public class InvalidExtensionException extends FileUploadException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    private String[] allowedExtension; // 允许的文件后缀数组
    private String extension; // 实际上传的文件后缀
    private String filename; // 文件名

    public InvalidExtensionException(String[] allowedExtension, String extension, String filename) // 构造方法
    {
        super("文件[" + filename + "]后缀[" + extension + "]不正确，请上传" + Arrays.toString(allowedExtension) + "格式"); // 拼接错误信息
        this.allowedExtension = allowedExtension;
        this.extension = extension;
        this.filename = filename;
    }

    public String[] getAllowedExtension() // 获取允许的后缀数组
    {
        return allowedExtension;
    }

    public String getExtension() // 获取实际后缀
    {
        return extension;
    }

    public String getFilename() // 获取文件名
    {
        return filename;
    }

    // 无效图片后缀异常内部类
    public static class InvalidImageExtensionException extends InvalidExtensionException
    {
        private static final long serialVersionUID = 1L; // 序列化版本号

        public InvalidImageExtensionException(String[] allowedExtension, String extension, String filename)
        {
            super(allowedExtension, extension, filename);
        }
    }

    // 无效Flash后缀异常内部类
    public static class InvalidFlashExtensionException extends InvalidExtensionException
    {
        private static final long serialVersionUID = 1L; // 序列化版本号

        public InvalidFlashExtensionException(String[] allowedExtension, String extension, String filename)
        {
            super(allowedExtension, extension, filename);
        }
    }

    // 无效媒体后缀异常内部类
    public static class InvalidMediaExtensionException extends InvalidExtensionException
    {
        private static final long serialVersionUID = 1L; // 序列化版本号

        public InvalidMediaExtensionException(String[] allowedExtension, String extension, String filename)
        {
            super(allowedExtension, extension, filename);
        }
    }

    // 无效视频后缀异常内部类
    public static class InvalidVideoExtensionException extends InvalidExtensionException
    {
        private static final long serialVersionUID = 1L; // 序列化版本号

        public InvalidVideoExtensionException(String[] allowedExtension, String extension, String filename)
        {
            super(allowedExtension, extension, filename);
        }
    }
}
