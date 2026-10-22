// 包声明
package com.debuff.generator.util;

// 导入Properties和Velocity引擎
import java.util.Properties;
import org.apache.velocity.app.Velocity;
import com.debuff.common.constant.Constants;

/**
 * VelocityEngine工厂
 * 
 * @author ruoyi
 */
// Velocity模板引擎初始化类
public class VelocityInitializer
{
    /**
     * 初始化vm方法
     */
    // 初始化Velocity模板引擎
    public static void initVelocity()
    {
        Properties p = new Properties(); // 创建配置属性对象
        try
        {
            // 加载classpath目录下的vm文件
            p.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"); // 设置类路径资源加载器
            // 定义字符集
            p.setProperty(Velocity.INPUT_ENCODING, Constants.UTF8); // 设置UTF-8编码
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p); // 初始化引擎
        }
        catch (Exception e)
        {
            throw new RuntimeException(e); // 初始化失败抛出运行时异常
        }
    }
}
