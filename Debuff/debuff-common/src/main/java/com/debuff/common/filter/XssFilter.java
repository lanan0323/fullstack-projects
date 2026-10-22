// 包声明
package com.debuff.common.filter;

// 导入Servlet过滤器相关类和工具类
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.enums.HttpMethod;

/**
 * 防止XSS攻击的过滤器
 * 
 * @author ruoyi
 */
public class XssFilter implements Filter
{
    /**
     * 排除链接
     */
    public List<String> excludes = new ArrayList<>(); // 不需要XSS过滤的URL列表

    @Override
    public void init(FilterConfig filterConfig) throws ServletException // 初始化过滤器,读取排除URL配置
    {
        String tempExcludes = filterConfig.getInitParameter("excludes"); // 从配置中获取排除URL
        if (StringUtils.isNotEmpty(tempExcludes)) // 配置不为空时解析
        {
            String[] url = tempExcludes.split(","); // 按逗号分隔
            for (int i = 0; url != null && i < url.length; i++)
            {
                excludes.add(url[i]); // 逐个加入排除列表
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException // 过滤逻辑
    {
        HttpServletRequest req = (HttpServletRequest) request; // 转为HttpServletRequest
        HttpServletResponse resp = (HttpServletResponse) response; // 转为HttpServletResponse
        if (handleExcludeURL(req, resp)) // 如果是排除URL则直接放行
        {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request); // 包装请求进行XSS过滤
        chain.doFilter(xssRequest, response); // 继续过滤链
    }

    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) // 判断是否排除URL
    {
        String url = request.getServletPath(); // 获取请求路径
        String method = request.getMethod(); // 获取请求方法
        // GET DELETE 不过滤
        if (method == null || HttpMethod.GET.matches(method) || HttpMethod.DELETE.matches(method)) // GET和DELETE请求不过滤
        {
            return true;
        }
        return StringUtils.matches(url, excludes); // 检查URL是否在排除列表中
    }

    @Override
    public void destroy() // 销毁过滤器(空实现)
    {

    }
}
