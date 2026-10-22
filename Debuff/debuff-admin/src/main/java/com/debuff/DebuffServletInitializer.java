// 包声明
package com.debuff;

// 导入Spring Boot构建器和Servlet支持类
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * web容器中进行部署
 * 
 * @author ruoyi
 */
// 继承SpringBootServletInitializer以支持外部Tomcat部署
public class DebuffServletInitializer extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        // 配置启动类为DebuffApplication
        return application.sources(DebuffApplication.class);
    }
}
