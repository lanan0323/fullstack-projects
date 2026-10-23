// 包声明
package com.debuff.common.filter;

// 导入IO和Servlet相关类
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.html.EscapeUtil;

/**
 * XSS过滤处理
 * 
 * @author ruoyi
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper
{
    /**
     * @param request
     */
    public XssHttpServletRequestWrapper(HttpServletRequest request) // 构造方法,包装原始request
    {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) // 获取参数值时进行XSS过滤
    {
        String[] values = super.getParameterValues(name); // 获取原始参数值
        if (values != null)
        {
            int length = values.length; // 参数值数组长度
            String[] escapesValues = new String[length]; // 创建过滤后的数组
            for (int i = 0; i < length; i++)
            {
                // 防xss攻击和过滤前后空格
                escapesValues[i] = EscapeUtil.clean(values[i]).trim(); // 清除HTML标签并去除空白
            }
            return escapesValues; // 返回过滤后的参数值
        }
        return super.getParameterValues(name); // 无参数时返回原始值
    }

    @Override
    public ServletInputStream getInputStream() throws IOException // 获取输入流时进行XSS过滤
    {
        // 非json类型，直接返回
        if (!isJsonRequest()) // 非JSON请求不过滤
        {
            return super.getInputStream();
        }

        // 为空，直接返回
        String json = IOUtils.toString(super.getInputStream(), "utf-8"); // 读取请求体JSON
        if (StringUtils.isEmpty(json)) // JSON为空直接返回
        {
            return super.getInputStream();
        }

        // xss过滤
        json = EscapeUtil.clean(json).trim(); // 清除JSON中的HTML标签
        byte[] jsonBytes = json.getBytes("utf-8"); // 转为字节数组
        final ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes); // 创建新的输入流
        return new ServletInputStream() // 返回包装后的输入流
        {
            @Override
            public boolean isFinished() // 流已读完
            {
                return true;
            }

            @Override
            public boolean isReady() // 流就绪
            {
                return true;
            }

            @Override
            public int available() throws IOException // 可用字节数
            {
                return jsonBytes.length;
            }

            @Override
            public void setReadListener(ReadListener readListener) // 设置读监听器(空实现)
            {
            }

            @Override
            public int read() throws IOException // 读取一个字节
            {
                return bis.read();
            }
        };
    }

    /**
     * 是否是Json请求
     * 
     * @param request
     */
    public boolean isJsonRequest() // 判断是否为JSON请求
    {
        String header = super.getHeader(HttpHeaders.CONTENT_TYPE); // 获取Content-Type头
        return StringUtils.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE); // 检查是否为application/json
    }
}
