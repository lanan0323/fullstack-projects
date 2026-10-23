// 包声明
package com.debuff.framework.manager;

// 导入所需类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;

/**
 * 确保应用退出时能关闭后台线程
 *
 * @author ruoyi
 */
@Component // 注册为Spring Bean
public class ShutdownManager
{
    private static final Logger logger = LoggerFactory.getLogger("sys-user"); // 日志记录器

    @PreDestroy // Spring容器销毁时自动调用
    public void destroy()
    {
        shutdownAsyncManager(); // 关闭异步任务管理器
    }

    /**
     * 停止异步执行任务
     */
    private void shutdownAsyncManager() // 关闭异步任务线程池
    {
        try
        {
            logger.info("====关闭后台任务任务线程池===="); // 打印关闭日志
            AsyncManager.me().shutdown(); // 调用异步管理器的关闭方法
        }
        catch (Exception e) // 捕获关闭过程中的异常
        {
            logger.error(e.getMessage(), e); // 打印错误日志
        }
    }
}
