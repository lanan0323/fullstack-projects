// 包声明
package com.debuff.common.utils;

// 导入线程池和日志相关类
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程相关工具类.
 * 
 * @author ruoyi
 */
public class Threads
{
    private static final Logger logger = LoggerFactory.getLogger(Threads.class); // 日志记录器

    /**
     * sleep等待,单位为毫秒
     */
    public static void sleep(long milliseconds) // 线程休眠指定毫秒
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) // 被中断时直接返回
        {
            return;
        }
    }

    /**
     * 停止线程池
     * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
     * 如果超时, 则调用shutdownNow, 取消在workQueue中Pending的任务,并中断所有阻塞函数.
     * 如果仍然超時，則強制退出.
     * 另对在shutdown时线程本身被调用中断做了处理.
     */
    public static void shutdownAndAwaitTermination(ExecutorService pool) // 优雅关闭线程池
    {
        if (pool != null && !pool.isShutdown()) // 线程池非空且未关闭
        {
            pool.shutdown(); // 停止接收新任务
            try
            {
                if (!pool.awaitTermination(120, TimeUnit.SECONDS)) // 等待120秒
                {
                    pool.shutdownNow(); // 超时则强制关闭
                    if (!pool.awaitTermination(120, TimeUnit.SECONDS)) // 再等120秒
                    {
                        logger.info("Pool did not terminate"); // 仍未关闭则记录日志
                    }
                }
            }
            catch (InterruptedException ie) // 等待时被中断
            {
                pool.shutdownNow(); // 强制关闭
                Thread.currentThread().interrupt(); // 恢复中断状态
            }
        }
    }

    /**
     * 打印线程异常信息
     */
    public static void printException(Runnable r, Throwable t) // 打印线程执行的异常信息
    {
        if (t == null && r instanceof Future<?>) // 异常为空且是Future类型
        {
            try
            {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) // 任务已完成
                {
                    future.get(); // 尝试获取结果(可能抛异常)
                }
            }
            catch (CancellationException ce) // 任务被取消
            {
                t = ce;
            }
            catch (ExecutionException ee) // 执行异常
            {
                t = ee.getCause(); // 获取根因
            }
            catch (InterruptedException ie) // 被中断
            {
                Thread.currentThread().interrupt(); // 恢复中断状态
            }
        }
        if (t != null) // 有异常则记录
        {
            logger.error(t.getMessage(), t);
        }
    }
}
