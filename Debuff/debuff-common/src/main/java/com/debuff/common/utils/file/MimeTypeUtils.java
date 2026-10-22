// 包声明：媒体类型工具类所在包
package com.debuff.common.utils.file;

/**
 * 媒体类型工具类
 * 
 * @author ruoyi
 */
public class MimeTypeUtils // 媒体类型工具类，定义各种MIME类型常量和允许上传的文件扩展名
{
    public static final String IMAGE_PNG = "image/png"; // PNG的MIME类型

    public static final String IMAGE_JPG = "image/jpg"; // JPG的MIME类型

    public static final String IMAGE_JPEG = "image/jpeg"; // JPEG的MIME类型

    public static final String IMAGE_BMP = "image/bmp"; // BMP的MIME类型

    public static final String IMAGE_GIF = "image/gif"; // GIF的MIME类型
    
    public static final String[] IMAGE_EXTENSION = { "bmp", "gif", "jpg", "jpeg", "png" }; // 图片类型扩展名数组

    public static final String[] FLASH_EXTENSION = { "swf", "flv" }; // Flash类型扩展名数组

    public static final String[] MEDIA_EXTENSION = { "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", // 媒体类型扩展名数组
            "asf", "rm", "rmvb" };

    public static final String[] VIDEO_EXTENSION = { "mp4", "avi", "rmvb" }; // 视频类型扩展名数组

    public static final String[] DEFAULT_ALLOWED_EXTENSION = { // 默认允许上传的文件扩展名
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png", // 图片格式
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt", // 办公文档格式
            // 压缩文件
            "rar", "zip", "gz", "bz2", // 压缩格式
            // 视频格式
            "mp4", "avi", "rmvb", // 视频格式
            // pdf
            "pdf" }; // PDF格式

    public static String getExtension(String prefix) // 根据MIME类型前缀获取文件扩展名
    {
        switch (prefix) // 匹配MIME类型
        {
            case IMAGE_PNG: // PNG
                return "png"; // 返回png
            case IMAGE_JPG: // JPG
                return "jpg"; // 返回jpg
            case IMAGE_JPEG: // JPEG
                return "jpeg"; // 返回jpeg
            case IMAGE_BMP: // BMP
                return "bmp"; // 返回bmp
            case IMAGE_GIF: // GIF
                return "gif"; // 返回gif
            default: // 未知类型
                return ""; // 返回空字符串
        }
    }
}
