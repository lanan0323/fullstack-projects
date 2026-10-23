// 包声明
package com.debuff.common.filter;

// 导入IO和Servlet相关类
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import com.debuff.common.utils.http.HttpHelper;
import com.debuff.common.constant.Constants;

/**
 * 构建可重复读取inputStream的request
 * 
 * @author ruoyi
 */
public class RepeatedlyRequestWrapper extends HttpServletRequestWrapper
{
    private final byte[] body; // 缓存请求体字节数组

    public RepeatedlyRequestWrapper(HttpServletRequest request, ServletResponse response) throws IOException // 构造方法,读取并缓存请求体
    {
        super(request);
        request.setCharacterEncoding(Constants.UTF8); // 设置请求编码为UTF-8
        response.setCharacterEncoding(Constants.UTF8); // 设置响应编码为UTF-8

        body = HttpHelper.getBodyString(request).getBytes(Constants.UTF8); // 读取请求体并缓存为字节数组
    }

    @Override
    public BufferedReader getReader() throws IOException // 获取BufferedReader,每次从缓存读取
    {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException // 获取ServletInputStream,从缓存字节数组创建
    {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body); // 从缓存创建输入流
        return new ServletInputStream() // 返回包装后的输入流
        {
            @Override
            public int read() throws IOException // 读取一个字节
            {
                return bais.read();
            }

            @Override
            public int available() throws IOException // 可用字节数
            {
                return body.length;
            }

            @Override
            public boolean isFinished() // 流未完成(可重复读)
            {
                return false;
            }

            @Override
            public boolean isReady() // 流未就绪
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) // 设置读监听器(空实现)
            {

            }
        };
    }
}
