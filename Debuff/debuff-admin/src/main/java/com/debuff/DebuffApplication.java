// 包声明
package com.debuff;

// 导入Spring Boot启动和自动配置相关依赖
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author ruoyi
 */
// 标记为Spring Boot启动类，排除数据源自动配置（使用自定义数据源）
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class DebuffApplication
{
    // 程序入口方法
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(DebuffApplication.class, args); // 启动Spring Boot应用
        System.out.println("(♥◠‿◠)ﾉﾞ  Debuff启动成功   ლ(´ڡ`ლ)ﾞ  \n"); // 打印启动成功提示
    }
}
