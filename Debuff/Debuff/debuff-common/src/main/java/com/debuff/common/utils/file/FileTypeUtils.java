// 包声明：文件类型工具类所在包
package com.debuff.common.utils.file;

// 导入File类和Apache字符串工具
import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件类型工具类
 *
 * @author ruoyi
 */
public class FileTypeUtils // 文件类型工具类，提供获取文件后缀名的方法
{
    /**
     * 获取文件类型
     * <p>
     * 例如: ruoyi.txt, 返回: txt
     * 
     * @param file 文件名
     * @return 后缀（不含".")
     */
    public static String getFileType(File file) // 根据File对象获取文件后缀名
    {
        if (null == file) // 文件为空时
        {
            return StringUtils.EMPTY; // 返回空字符串
        }
        return getFileType(file.getName()); // 递归调用，传入文件名
    }

    /**
     * 获取文件类型
     * <p>
     * 例如: ruoyi.txt, 返回: txt
     *
     * @param fileName 文件名
     * @return 后缀（不含".")
     */
    public static String getFileType(String fileName) // 根据文件名字符串获取后缀名
    {
        int separatorIndex = fileName.lastIndexOf("."); // 查找最后一个"."的位置
        if (separatorIndex < 0) // 没有"."时
        {
            return ""; // 返回空字符串
        }
        return fileName.substring(separatorIndex + 1).toLowerCase(); // 截取"."后的部分并转小写
    }

    /**
     * 获取文件类型
     * 
     * @param photoByte 文件字节码
     * @return 后缀（不含".")
     */
    public static String getFileExtendName(byte[] photoByte) // 根据文件字节头判断文件类型
    {
        String strFileExtendName = "JPG"; // 默认为JPG格式
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56) // 判断是否为GIF文件头
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97))
        {
            strFileExtendName = "GIF"; // 设置为GIF
        }
        else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) // 判断是否为JPG文件头
        {
            strFileExtendName = "JPG"; // 设置为JPG
        }
        else if ((photoByte[0] == 66) && (photoByte[1] == 77)) // 判断是否为BMP文件头
        {
            strFileExtendName = "BMP"; // 设置为BMP
        }
        else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) // 判断是否为PNG文件头
        {
            strFileExtendName = "PNG"; // 设置为PNG
        }
        return strFileExtendName; // 返回识别到的文件类型
    }
}
