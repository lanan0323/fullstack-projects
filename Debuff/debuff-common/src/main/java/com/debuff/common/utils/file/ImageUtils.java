// 包声明：图片处理工具类所在包
package com.debuff.common.utils.file;

// 导入字节流、网络URL、Apache POI、日志、配置、常量、字符串工具等
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.constant.Constants;
import com.debuff.common.utils.StringUtils;

/**
 * 图片处理工具类
 *
 * @author ruoyi
 */
public class ImageUtils // 图片处理工具类，支持本地和网络图片读取
{
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class); // 日志对象

    public static byte[] getImage(String imagePath) // 获取图片的字节数组
    {
        InputStream is = getFile(imagePath); // 获取图片输入流
        try
        {
            return IOUtils.toByteArray(is); // 将输入流转为字节数组
        }
        catch (Exception e)
        {
            log.error("图片加载异常 {}", e); // 记录错误日志
            return null; // 返回null
        }
        finally
        {
            IOUtils.closeQuietly(is); // 安静关闭输入流
        }
    }

    public static InputStream getFile(String imagePath) // 获取图片的输入流
    {
        try
        {
            byte[] result = readFile(imagePath); // 读取文件为字节数组
            result = Arrays.copyOf(result, result.length); // 复制字节数组
            return new ByteArrayInputStream(result); // 转为字节输入流返回
        }
        catch (Exception e)
        {
            log.error("获取图片异常 {}", e); // 记录错误日志
        }
        return null; // 失败返回null
    }

    /**
     * 读取文件为字节数据
     * 
     * @param url 地址
     * @return 字节数据
     */
    public static byte[] readFile(String url) // 根据URL读取文件为字节数组，支持http和本地路径
    {
        InputStream in = null; // 输入流
        try
        {
            if (url.startsWith("http")) // http开头的为网络地址
            {
                // 网络地址
                URL urlObj = new URL(url); // 创建URL对象
                URLConnection urlConnection = urlObj.openConnection(); // 打开连接
                urlConnection.setConnectTimeout(30 * 1000); // 设置连接超时30秒
                urlConnection.setReadTimeout(60 * 1000); // 设置读取超时60秒
                urlConnection.setDoInput(true); // 允许输入
                in = urlConnection.getInputStream(); // 获取输入流
            }
            else
            {
                // 本机地址
                String localPath = RuoYiConfig.getProfile(); // 获取本地上传根路径
                String downloadPath = localPath + StringUtils.substringAfter(url, Constants.RESOURCE_PREFIX); // 拼接完整本地路径
                in = new FileInputStream(downloadPath); // 创建文件输入流
            }
            return IOUtils.toByteArray(in); // 将输入流转为字节数组
        }
        catch (Exception e)
        {
            log.error("获取文件路径异常 {}", e); // 记录错误日志
            return null; // 返回null
        }
        finally
        {
            IOUtils.closeQuietly(in); // 安静关闭输入流
        }
    }
}
