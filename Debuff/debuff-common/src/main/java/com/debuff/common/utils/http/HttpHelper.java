// 包声明：HTTP工具类所在包
package com.debuff.common.utils.http;

// 导入IO流、字符集、Servlet请求、日志等相关类
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用http工具封装
 * 
 * @author ruoyi
 */
public class HttpHelper // HTTP请求辅助工具类
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class); // 日志对象

    public static String getBodyString(ServletRequest request) // 读取请求体的字符串内容
    {
        StringBuilder sb = new StringBuilder(); // 创建字符串构建器
        BufferedReader reader = null; // 缓冲读取器
        try (InputStream inputStream = request.getInputStream()) // 获取请求输入流
        {
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)); // 创建UTF-8缓冲读取器
            String line = ""; // 每行内容
            while ((line = reader.readLine()) != null) // 逐行读取
            {
                sb.append(line); // 追加到结果
            }
        }
        catch (IOException e)
        {
            LOGGER.warn("getBodyString出现问题！"); // 记录警告日志
        }
        finally
        {
            if (reader != null) // 读取器不为空时
            {
                try
                {
                    reader.close(); // 关闭读取器
                }
                catch (IOException e)
                {
                    LOGGER.error(ExceptionUtils.getMessage(e)); // 记录错误日志
                }
            }
        }
        return sb.toString(); // 返回请求体字符串
    }
}
