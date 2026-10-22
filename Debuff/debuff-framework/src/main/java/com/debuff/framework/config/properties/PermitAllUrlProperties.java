// 包声明
package com.debuff.framework.config.properties;

// 导入所需类
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.debuff.common.annotation.Anonymous;

/**
 * 设置Anonymous注解允许匿名访问的url
 * 
 * @author ruoyi
 */
@Configuration // 标记为配置类
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware // 实现初始化Bean和ApplicationContext感知接口
{
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}"); // 匹配路径变量的正则表达式，如{id}

    private ApplicationContext applicationContext; // Spring应用上下文

    private List<String> urls = new ArrayList<>(); // 存储允许匿名访问的URL列表

    public String ASTERISK = "*"; // 通配符，用于替换路径变量

    @Override
    public void afterPropertiesSet() // Bean属性设置完成后的初始化方法
    {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class); // 获取请求映射处理器
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods(); // 获取所有请求映射和对应的处理方法

        map.keySet().forEach(info -> { // 遍历所有映射
            HandlerMethod handlerMethod = map.get(info); // 获取处理方法

            // 获取方法上边的注解 替代path variable 为 *
            Anonymous method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class); // 查找方法上的@Anonymous注解
            Optional.ofNullable(method).ifPresent(anonymous -> Objects.requireNonNull(info.getPatternsCondition().getPatterns()) // 如果存在注解则收集URL
                    .forEach(url -> urls.add(RegExUtils.replaceAll(url, PATTERN, ASTERISK)))); // 将路径变量替换为通配符*

            // 获取类上边的注解, 替代path variable 为 *
            Anonymous controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class); // 查找类上的@Anonymous注解
            Optional.ofNullable(controller).ifPresent(anonymous -> Objects.requireNonNull(info.getPatternsCondition().getPatterns()) // 如果存在注解则收集URL
                    .forEach(url -> urls.add(RegExUtils.replaceAll(url, PATTERN, ASTERISK)))); // 将路径变量替换为通配符*
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException // 设置Spring应用上下文
    {
        this.applicationContext = context; // 保存应用上下文引用
    }

    public List<String> getUrls() // 获取允许匿名访问的URL列表
    {
        return urls;
    }

    public void setUrls(List<String> urls) // 设置允许匿名访问的URL列表
    {
        this.urls = urls;
    }
}
