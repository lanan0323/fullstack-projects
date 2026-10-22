// 包声明
package com.debuff.framework.config;

// 导入所需类
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import com.debuff.common.enums.DataSourceType;
import com.debuff.common.utils.spring.SpringUtils;
import com.debuff.framework.config.properties.DruidProperties;
import com.debuff.framework.datasource.DynamicDataSource;

/**
 * druid 配置多数据源
 * 
 * @author ruoyi
 */
@Configuration // 标记为配置类
public class DruidConfig
{
    @Bean // 注册主数据源Bean
    @ConfigurationProperties("spring.datasource.druid.master") // 绑定配置文件中master数据源的配置
    public DataSource masterDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build(); // 创建Druid数据源实例
        return druidProperties.dataSource(dataSource); // 应用Druid连接池属性并返回
    }

    @Bean // 注册从数据源Bean
    @ConfigurationProperties("spring.datasource.druid.slave") // 绑定配置文件中slave数据源的配置
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true") // 仅当配置了slave.enabled=true时才创建
    public DataSource slaveDataSource(DruidProperties druidProperties)
    {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build(); // 创建Druid数据源实例
        return druidProperties.dataSource(dataSource); // 应用Druid连接池属性并返回
    }

    @Bean(name = "dynamicDataSource") // 注册动态数据源Bean
    @Primary // 标记为主数据源，多数据源时优先使用
    public DynamicDataSource dataSource(DataSource masterDataSource)
    {
        Map<Object, Object> targetDataSources = new HashMap<>(); // 创建目标数据源映射
        targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource); // 添加主数据源
        setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource"); // 尝试添加从数据源
        return new DynamicDataSource(masterDataSource, targetDataSources); // 创建并返回动态数据源
    }
    
    /**
     * 设置数据源
     * 
     * @param targetDataSources 备选数据源集合
     * @param sourceName 数据源名称
     * @param beanName bean名称
     */
    public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName) // 根据beanName从Spring容器获取数据源并添加到映射中
    {
        try
        {
            DataSource dataSource = SpringUtils.getBean(beanName); // 从Spring容器中获取数据源Bean
            targetDataSources.put(sourceName, dataSource); // 添加到目标数据源映射
        }
        catch (Exception e) // 如果Bean不存在则忽略（从数据源可能未配置）
        {
        }
    }

    /**
     * 去除监控页面底部的广告
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean // 注册过滤器
    @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true") // 仅当Druid监控页面启用时才创建
    public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties)
    {
        // 获取web监控页面的参数
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet(); // 获取StatViewServlet配置
        // 提取common.js的配置路径
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*"; // 获取监控页面URL模式
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js"); // 拼接common.js的URL路径
        final String filePath = "support/http/resources/js/common.js"; // common.js在Druid jar包中的资源路径
        // 创建filter进行过滤
        Filter filter = new Filter() // 创建匿名过滤器，用于去除Druid监控页面的广告
        {
            @Override
            public void init(javax.servlet.FilterConfig filterConfig) throws ServletException
            {
            }
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException
            {
                chain.doFilter(request, response); // 先执行过滤链
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer(); // 清空响应缓冲区
                // 获取common.js
                String text = Utils.readFromResource(filePath); // 读取common.js文件内容
                // 正则替换banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", ""); // 移除banner广告HTML
                text = text.replaceAll("powered.*?shrek.wang</a>", ""); // 移除powered by广告文字
                response.getWriter().write(text); // 写入过滤后的JS内容
            }
            @Override
            public void destroy()
            {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(); // 创建过滤器注册Bean
        registrationBean.setFilter(filter); // 设置过滤器
        registrationBean.addUrlPatterns(commonJsPattern); // 设置过滤的URL模式
        return registrationBean; // 返回注册Bean
    }
}
