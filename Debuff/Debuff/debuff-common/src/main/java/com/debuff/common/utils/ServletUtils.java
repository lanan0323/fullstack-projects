// 包声明
package com.debuff.common.utils;

// 导入Servlet和编解码相关类
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.text.Convert;

/**
 * 客户端工具类
 * 
 * @author ruoyi
 */
public class ServletUtils
{
    /**
     * 获取String参数
     */
    public static String getParameter(String name) // 获取请求参数(String)
    {
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) // 获取请求参数(带默认值)
    {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) // 获取请求参数(Integer)
    {
        return Convert.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) // 获取请求参数(带默认值)
    {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name) // 获取请求参数(Boolean)
    {
        return Convert.toBool(getRequest().getParameter(name));
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name, Boolean defaultValue) // 获取请求参数(带默认值)
    {
        return Convert.toBool(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) // 获取所有请求参数(不可变Map)
    {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map); // 返回不可修改的Map
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String> getParamMap(ServletRequest request) // 获取所有请求参数(值合并为逗号分隔字符串)
    {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) // 遍历所有参数
        {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), ",")); // 多值用逗号拼接
        }
        return params;
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() // 从RequestContextHolder获取当前请求
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() // 从RequestContextHolder获取当前响应
    {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() // 获取当前请求的Session
    {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() // 获取ServletRequestAttributes
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     * 
     * @param response 渲染对象
     * @param string 待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) // 将JSON字符串写入响应
    {
        try
        {
            response.setStatus(200); // 设置状态码
            response.setContentType("application/json"); // 设置内容类型
            response.setCharacterEncoding("utf-8"); // 设置编码
            response.getWriter().print(string); // 写入响应体
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 是否是Ajax异步请求
     * 
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) // 判断是否为Ajax请求
    {
        String accept = request.getHeader("accept"); // 检查Accept头
        if (accept != null && accept.contains("application/json"))
        {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With"); // 检查X-Requested-With头
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest"))
        {
            return true;
        }

        String uri = request.getRequestURI(); // 检查URL后缀
        if (StringUtils.inStringIgnoreCase(uri, ".json", ".xml"))
        {
            return true;
        }

        String ajax = request.getParameter("__ajax"); // 检查__ajax参数
        return StringUtils.inStringIgnoreCase(ajax, "json", "xml");
    }

    /**
     * 内容编码
     * 
     * @param str 内容
     * @return 编码后的内容
     */
    public static String urlEncode(String str) // URL编码(UTF-8)
    {
        try
        {
            return URLEncoder.encode(str, Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 内容解码
     * 
     * @param str 内容
     * @return 解码后的内容
     */
    public static String urlDecode(String str) // URL解码(UTF-8)
    {
        try
        {
            return URLDecoder.decode(str, Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            return StringUtils.EMPTY;
        }
    }
}
