// 包声明：文件处理工具类所在包
package com.debuff.common.utils.file;

// 导入文件IO、网络、Servlet、Apache工具等相关类
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.uuid.IdUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * 文件处理工具类
 * 
 * @author ruoyi
 */
public class FileUtils // 文件处理工具类，提供文件读写、删除、下载编码等功能
{
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+"; // 合法文件名正则：字母数字下划线横杠点中文

    /**
     * 输出指定文件的byte数组
     * 
     * @param filePath 文件路径
     * @param os 输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException // 将文件内容写入输出流
    {
        FileInputStream fis = null; // 文件输入流
        try
        {
            File file = new File(filePath); // 创建文件对象
            if (!file.exists()) // 文件不存在时
            {
                throw new FileNotFoundException(filePath); // 抛出文件未找到异常
            }
            fis = new FileInputStream(file); // 创建文件输入流
            byte[] b = new byte[1024]; // 创建1KB缓冲区
            int length; // 每次读取的字节数
            while ((length = fis.read(b)) > 0) // 循环读取文件
            {
                os.write(b, 0, length); // 写入输出流
            }
        }
        catch (IOException e)
        {
            throw e; // 向上抛出IO异常
        }
        finally
        {
            IOUtils.close(os); // 关闭输出流
            IOUtils.close(fis); // 关闭输入流
        }
    }

    /**
     * 写数据到文件中
     *
     * @param data 数据
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static String writeImportBytes(byte[] data) throws IOException // 将字节数据写入导入目录
    {
        return writeBytes(data, RuoYiConfig.getImportPath()); // 调用writeBytes写入导入路径
    }

    /**
     * 写数据到文件中
     *
     * @param data 数据
     * @param uploadDir 目标文件
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static String writeBytes(byte[] data, String uploadDir) throws IOException // 将字节数据写入指定上传目录
    {
        FileOutputStream fos = null; // 文件输出流
        String pathName = ""; // 生成的文件路径名
        try
        {
            String extension = getFileExtendName(data); // 根据字节头获取文件后缀
            pathName = DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + extension; // 生成日期/UUID.后缀的路径
            File file = FileUploadUtils.getAbsoluteFile(uploadDir, pathName); // 获取绝对路径文件对象
            fos = new FileOutputStream(file); // 创建文件输出流
            fos.write(data); // 写入数据
        }
        finally
        {
            IOUtils.close(fos); // 关闭输出流
        }
        return FileUploadUtils.getPathFileName(uploadDir, pathName); // 返回可访问的文件路径
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) // 删除指定路径的文件
    {
        boolean flag = false; // 删除结果标志
        File file = new File(filePath); // 创建文件对象
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) // 确认是文件且存在
        {
            flag = file.delete(); // 执行删除操作
        }
        return flag; // 返回删除结果
    }

    /**
     * 文件名称验证
     * 
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) // 验证文件名是否合法
    {
        return filename.matches(FILENAME_PATTERN); // 用正则表达式匹配文件名
    }

    /**
     * 检查文件是否可下载
     * 
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource) // 检查文件是否允许下载
    {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, "..")) // 包含..防止路径穿越
        {
            return false; // 不允许下载
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource))) // 检查文件类型是否在允许列表中
        {
            return true; // 允许下载
        }

        // 不在允许下载的文件规则
        return false; // 不允许下载
    }

    /**
     * 下载文件名重新编码
     * 
     * @param request 请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException // 根据浏览器类型编码文件名
    {
        final String agent = request.getHeader("USER-AGENT"); // 获取浏览器User-Agent
        String filename = fileName; // 初始化文件名
        if (agent.contains("MSIE")) // IE浏览器
        {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8"); // URL编码
            filename = filename.replace("+", " "); // 将+替换为空格
        }
        else if (agent.contains("Firefox")) // 火狐浏览器
        {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1"); // 转为ISO8859-1编码
        }
        else if (agent.contains("Chrome")) // google浏览器
        {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8"); // URL编码
        }
        else
        {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8"); // URL编码
        }
        return filename; // 返回编码后的文件名
    }

    /**
     * 下载文件名重新编码
     *
     * @param response 响应对象
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException // 设置附件下载的响应头
    {
        String percentEncodedFileName = percentEncode(realFileName); // 百分号编码文件名

        StringBuilder contentDispositionValue = new StringBuilder(); // 构建Content-Disposition值
        contentDispositionValue.append("attachment; filename=") // 追加attachment和filename
                .append(percentEncodedFileName) // 追加编码后的文件名
                .append(";") // 分号分隔
                .append("filename*=") // 追加filename*字段
                .append("utf-8''") // UTF-8编码标记
                .append(percentEncodedFileName); // 追加编码后的文件名

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename"); // 暴露跨域头
        response.setHeader("Content-disposition", contentDispositionValue.toString()); // 设置Content-Disposition头
        response.setHeader("download-filename", percentEncodedFileName); // 设置下载文件名头
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException // 百分号编码，空格编码为%20
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString()); // URL编码
        return encode.replaceAll("\\+", "%20"); // 将+替换为%20
    }

    /**
     * 获取图像后缀
     * 
     * @param photoByte 图像数据
     * @return 后缀名
     */
    public static String getFileExtendName(byte[] photoByte) // 根据字节头判断图片后缀
    {
        String strFileExtendName = "jpg"; // 默认为jpg
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56) // 判断GIF文件头
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97))
        {
            strFileExtendName = "gif"; // 设置为gif
        }
        else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) // 判断JPG文件头
        {
            strFileExtendName = "jpg"; // 设置为jpg
        }
        else if ((photoByte[0] == 66) && (photoByte[1] == 77)) // 判断BMP文件头
        {
            strFileExtendName = "bmp"; // 设置为bmp
        }
        else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) // 判断PNG文件头
        {
            strFileExtendName = "png"; // 设置为png
        }
        return strFileExtendName; // 返回识别到的后缀名
    }

    /**
     * 获取文件名称 /profile/upload/2022/04/16/ruoyi.png -- ruoyi.png
     * 
     * @param fileName 路径名称
     * @return 没有文件路径的名称
     */
    public static String getName(String fileName) // 从完整路径中提取文件名（含后缀）
    {
        if (fileName == null) // 文件名为空时
        {
            return null; // 返回null
        }
        int lastUnixPos = fileName.lastIndexOf('/'); // 查找最后一个/的位置
        int lastWindowsPos = fileName.lastIndexOf('\\'); // 查找最后一个\的位置
        int index = Math.max(lastUnixPos, lastWindowsPos); // 取两者较大值
        return fileName.substring(index + 1); // 截取文件名部分
    }

    /**
     * 获取不带后缀文件名称 /profile/upload/2022/04/16/ruoyi.png -- ruoyi
     * 
     * @param fileName 路径名称
     * @return 没有文件路径和后缀的名称
     */
    public static String getNameNotSuffix(String fileName) // 从完整路径中提取文件名（不含后缀）
    {
        if (fileName == null) // 文件名为空时
        {
            return null; // 返回null
        }
        String baseName = FilenameUtils.getBaseName(fileName); // 使用Apache工具获取不含后缀的文件名
        return baseName; // 返回不含后缀的文件名
    }
}
