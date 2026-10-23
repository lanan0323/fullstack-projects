// 包声明
package com.debuff.framework.manager;

// 导入所需类
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.debuff.common.utils.Threads;
import com.debuff.common.utils.spring.SpringUtils;

/**
 * 异步任务管理器
 * 
 * @author ruoyi
 */
public class AsyncManager
{
    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10; // 任务执行延迟时间（毫秒）

    /**
     * 异步操作任务调度线程池
     */
    private ScheduledExecutorService executor = SpringUtils.getBean("scheduledExecutorService"); // 从Spring容器获取定时任务线程池

    /**
     * 单例模式
     */
    private AsyncManager(){} // 私有构造函数，防止外部实例化

    private static AsyncManager me = new AsyncManager(); // 创建单例实例

    public static AsyncManager me() // 获取单例实例
    {
        return me;
    }

    /**
     * 执行任务
     * 
     * @param task 任务
     */
    public void execute(TimerTask task) // 提交定时任务到线程池延迟执行
    {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS); // 延迟10毫秒后执行任务
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() // 关闭异步任务线程池
    {
        Threads.shutdownAndAwaitTermination(executor); // 优雅关闭线程池
    }
}
