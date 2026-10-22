// 包声明
package com.debuff.framework.datasource;

// 导入所需类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源切换处理
 * 
 * @author ruoyi
 */
public class DynamicDataSourceContextHolder
{
    public static final Logger log = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class); // 日志记录器

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>(); // 线程本地变量，存储当前线程使用的数据源类型

    /**
     * 设置数据源的变量
     */
    public static void setDataSourceType(String dsType) // 设置当前线程的数据源类型
    {
        log.info("切换到{}数据源", dsType); // 打印切换数据源日志
        CONTEXT_HOLDER.set(dsType); // 将数据源类型存入线程本地变量
    }

    /**
     * 获得数据源的变量
     */
    public static String getDataSourceType() // 获取当前线程的数据源类型
    {
        return CONTEXT_HOLDER.get(); // 从线程本地变量中获取
    }

    /**
     * 清空数据源变量
     */
    public static void clearDataSourceType() // 清除当前线程的数据源类型，防止内存泄漏
    {
        CONTEXT_HOLDER.remove(); // 从线程本地变量中移除
    }
}
