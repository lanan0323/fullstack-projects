// 包声明
package com.debuff.framework.config;

// 导入所需类
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.debuff.common.utils.ServletUtils;

/**
 * 服务相关配置
 * 
 * @author ruoyi
 */
@Component // 注册为Spring Bean
public class ServerConfig
{
    /**
     * 获取完整的请求路径，包括：域名，端口，上下文访问路径
     * 
     * @return 服务地址
     */
    public String getUrl() // 获取当前服务的完整URL地址
    {
        HttpServletRequest request = ServletUtils.getRequest(); // 获取当前HTTP请求
        return getDomain(request); // 获取域名信息
    }

    public static String getDomain(HttpServletRequest request) // 从请求中提取域名部分（协议+域名+端口+上下文路径）
    {
        StringBuffer url = request.getRequestURL(); // 获取完整请求URL
        String contextPath = request.getServletContext().getContextPath(); // 获取应用上下文路径
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString(); // 截取域名部分并拼接上下文路径
    }
}
