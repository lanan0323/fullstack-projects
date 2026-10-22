// 包声明
package com.debuff.framework.config;

// 导入所需类
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.constant.Constants;
import com.debuff.framework.interceptor.RepeatSubmitInterceptor;

/**
 * 通用配置
 * 
 * @author ruoyi
 */
@Configuration // 标记为配置类
public class ResourcesConfig implements WebMvcConfigurer // 实现WebMvcConfigurer自定义Spring MVC配置
{
    @Autowired // 自动注入防重复提交拦截器
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) // 配置静态资源映射
    {
        /** 本地文件上传路径 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**") // 配置文件资源访问路径
                .addResourceLocations("file:" + RuoYiConfig.getProfile() + "/"); // 映射到本地文件目录

        /** swagger配置 */
        registry.addResourceHandler("/swagger-ui/**") // 配置Swagger UI静态资源路径
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/") // 映射到Swagger UI的jar包资源
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());; // 设置缓存有效期为5小时
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) // 注册自定义拦截器
    {
        registry.addInterceptor(repeatSubmitInterceptor).addPathPatterns("/**"); // 注册防重复提交拦截器，拦截所有请求
    }

    /**
     * 跨域配置
     */
    @Bean // 注册跨域过滤器Bean
    public CorsFilter corsFilter()
    {
        CorsConfiguration config = new CorsConfiguration(); // 创建CORS配置对象
        config.setAllowCredentials(true); // 允许携带Cookie凭证
        // 设置访问源地址
        config.addAllowedOriginPattern("*"); // 允许所有来源域名的跨域请求
        // 设置访问源请求头
        config.addAllowedHeader("*"); // 允许所有请求头
        // 设置访问源请求方法
        config.addAllowedMethod("*"); // 允许所有HTTP方法
        // 有效期 1800秒
        config.setMaxAge(1800L); // 设置预检请求的有效期为1800秒
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // 创建CORS配置源
        source.registerCorsConfiguration("/**", config); // 对所有路径应用CORS配置
        // 返回新的CorsFilter
        return new CorsFilter(source); // 返回跨域过滤器
    }
}
