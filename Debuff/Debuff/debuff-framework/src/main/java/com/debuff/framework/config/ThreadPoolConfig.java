// 包声明
package com.debuff.framework.config;

// 导入所需类
import com.debuff.common.utils.Threads;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author ruoyi
 **/
@Configuration // 标记为配置类
public class ThreadPoolConfig
{
    // 核心线程池大小
    private int corePoolSize = 50; // 核心线程数，线程池中保持存活的线程数量

    // 最大可创建的线程数
    private int maxPoolSize = 200; // 最大线程数，线程池允许创建的最大线程数量

    // 队列最大长度
    private int queueCapacity = 1000; // 任务队列容量，超过核心线程数的任务进入队列等待

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds = 300; // 空闲线程存活时间（秒），超过此时间的空闲线程将被回收

    @Bean(name = "threadPoolTaskExecutor") // 注册线程池任务执行器Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // 创建线程池任务执行器
        executor.setMaxPoolSize(maxPoolSize); // 设置最大线程数
        executor.setCorePoolSize(corePoolSize); // 设置核心线程数
        executor.setQueueCapacity(queueCapacity); // 设置队列容量
        executor.setKeepAliveSeconds(keepAliveSeconds); // 设置空闲线程存活时间
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 设置拒绝策略为调用者运行策略
        return executor; // 返回线程池执行器
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService") // 注册定时任务线程池Bean
    protected ScheduledExecutorService scheduledExecutorService()
    {
        return new ScheduledThreadPoolExecutor(corePoolSize, // 创建定时线程池
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(), // 设置线程命名模式和守护线程
                new ThreadPoolExecutor.CallerRunsPolicy()) // 设置拒绝策略
        {
            @Override
            protected void afterExecute(Runnable r, Throwable t) // 任务执行完毕后的回调
            {
                super.afterExecute(r, t); // 调用父类方法
                Threads.printException(r, t); // 打印任务执行过程中的异常
            }
        };
    }
}
