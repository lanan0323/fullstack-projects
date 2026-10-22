// 包声明
package com.debuff.generator.config;

// 导入Spring配置相关注解
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取代码生成相关配置
 * 
 * @author ruoyi
 */
// 注册为Spring组件
@Component
// 绑定gen前缀的配置属性
@ConfigurationProperties(prefix = "gen")
// 加载generator.yml配置文件
@PropertySource(value = { "classpath:generator.yml" })
// 代码生成配置类，读取generator.yml中的配置项
public class GenConfig
{
    /** 作者 */
    // 代码作者名称
    public static String author;

    /** 生成包路径 */
    // 生成代码的包路径
    public static String packageName;

    /** 自动去除表前缀，默认是false */
    // 是否自动去除表前缀
    public static boolean autoRemovePre;

    /** 表前缀(类名不会包含表前缀) */
    // 表前缀字符串，生成类名时会去掉
    public static String tablePrefix;

    // 获取作者
    public static String getAuthor()
    {
        return author;
    }

    // 注入作者配置值
    @Value("${author}")
    public void setAuthor(String author)
    {
        GenConfig.author = author;
    }

    // 获取包路径
    public static String getPackageName()
    {
        return packageName;
    }

    // 注入包路径配置值
    @Value("${packageName}")
    public void setPackageName(String packageName)
    {
        GenConfig.packageName = packageName;
    }

    // 获取是否自动去除前缀
    public static boolean getAutoRemovePre()
    {
        return autoRemovePre;
    }

    // 注入自动去除前缀配置值
    @Value("${autoRemovePre}")
    public void setAutoRemovePre(boolean autoRemovePre)
    {
        GenConfig.autoRemovePre = autoRemovePre;
    }

    // 获取表前缀
    public static String getTablePrefix()
    {
        return tablePrefix;
    }

    // 注入表前缀配置值
    @Value("${tablePrefix}")
    public void setTablePrefix(String tablePrefix)
    {
        GenConfig.tablePrefix = tablePrefix;
    }
}
