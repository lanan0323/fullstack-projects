// 包声明
package com.debuff.framework.config;

// 导入所需类
import java.util.HashMap;
import java.util.Map;
import javax.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.debuff.common.filter.RepeatableFilter;
import com.debuff.common.filter.XssFilter;
import com.debuff.common.utils.StringUtils;

/**
 * Filter配置
 *
 * @author ruoyi
 */
@Configuration // 标记为配置类
public class FilterConfig
{
    @Value("${xss.excludes}") // 从配置文件读取XSS排除路径
    private String excludes;

    @Value("${xss.urlPatterns}") // 从配置文件读取XSS过滤URL模式
    private String urlPatterns;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean // 注册XSS过滤器Bean
    @ConditionalOnProperty(value = "xss.enabled", havingValue = "true") // 仅当xss.enabled=true时才注册
    public FilterRegistrationBean xssFilterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean(); // 创建过滤器注册Bean
        registration.setDispatcherTypes(DispatcherType.REQUEST); // 设置过滤器分发类型为REQUEST
        registration.setFilter(new XssFilter()); // 设置XSS过滤器实例
        registration.addUrlPatterns(StringUtils.split(urlPatterns, ",")); // 设置需要过滤的URL模式
        registration.setName("xssFilter"); // 设置过滤器名称
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE); // 设置最高优先级
        Map<String, String> initParameters = new HashMap<String, String>(); // 创建初始化参数
        initParameters.put("excludes", excludes); // 设置排除路径参数
        registration.setInitParameters(initParameters); // 设置初始化参数
        return registration; // 返回注册Bean
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean // 注册可重复读过滤器Bean
    public FilterRegistrationBean someFilterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean(); // 创建过滤器注册Bean
        registration.setFilter(new RepeatableFilter()); // 设置可重复读过滤器，使请求体可以被多次读取
        registration.addUrlPatterns("/*"); // 拦截所有请求
        registration.setName("repeatableFilter"); // 设置过滤器名称
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE); // 设置最低优先级（最后执行）
        return registration; // 返回注册Bean
    }

}
