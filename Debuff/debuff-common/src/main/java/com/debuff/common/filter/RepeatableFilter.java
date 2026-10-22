// 包声明
package com.debuff.common.filter;

// 导入Servlet过滤器和Spring MediaType相关类
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import com.debuff.common.utils.StringUtils;

/**
 * Repeatable 过滤器
 * 
 * @author ruoyi
 */
public class RepeatableFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException // 初始化(空实现)
    {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException // 过滤逻辑
    {
        ServletRequest requestWrapper = null; // 可重复读取的request包装器
        if (request instanceof HttpServletRequest
                && StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) // 判断是否为JSON请求
        {
            requestWrapper = new RepeatedlyRequestWrapper((HttpServletRequest) request, response); // 包装为可重复读取的request
        }
        if (null == requestWrapper) // 非JSON请求直接用原始request
        {
            chain.doFilter(request, response);
        }
        else // JSON请求用包装后的request
        {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() // 销毁过滤器(空实现)
    {

    }
}
