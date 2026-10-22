// 包声明
package com.debuff.trade.config;

// 导入所需类
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 *
 * <p>Spring Boot 默认不启用定时任务调度，需要手动添加 @EnableScheduling 注解开启。
 * 加上此注解后，Spring 会扫描所有 Bean 中标注了 @Scheduled 的方法，
 * 按照配置的调度策略（fixedDelay/fixedRate/cron）自动触发执行。</p>
 *
 * <p>如果不加这个配置类，OrderTimeoutTask 中的 @Scheduled 注解不会生效，
 * 定时任务就永远不会执行。这是 Spring 的显式声明设计理念——功能开关由开发者主动打开。</p>
 *
 * @author debuff
 */
@Configuration // 标记为配置类
@EnableScheduling // 启用定时任务调度
public class ScheduleConfig
{
}
