// 包声明
package com.debuff.framework.config;

// 导入所需类
import java.util.TimeZone;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 *
 * @author ruoyi
 */
@Configuration // 标记为配置类
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true) // 开启AOP代理并暴露代理对象，使AopContext可以获取当前代理
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.debuff.**.mapper") // 扫描所有模块下的mapper包，自动注册Mapper接口
public class ApplicationConfig
{
    /**
     * 时区配置
     */
    @Bean // 注册Jackson时区定制器Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault()); // 设置Jackson日期序列化的时区为系统默认时区
    }
}
