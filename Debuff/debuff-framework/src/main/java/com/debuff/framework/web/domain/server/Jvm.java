// 包声明
package com.debuff.framework.web.domain.server;

// 导入所需类
import java.lang.management.ManagementFactory;
import com.debuff.common.utils.Arith;
import com.debuff.common.utils.DateUtils;

/**
 * JVM相关信息
 * 
 * @author ruoyi
 */
public class Jvm
{
    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    public double getTotal()
    {
        return Arith.div(total, (1024 * 1024), 2); // 转换为MB，保留2位小数
    }

    public void setTotal(double total)
    {
        this.total = total; // 设置JVM总内存
    }

    public double getMax()
    {
        return Arith.div(max, (1024 * 1024), 2); // 转换为MB，保留2位小数
    }

    public void setMax(double max)
    {
        this.max = max; // 设置JVM最大可用内存
    }

    public double getFree()
    {
        return Arith.div(free, (1024 * 1024), 2); // 转换为MB，保留2位小数
    }

    public void setFree(double free)
    {
        this.free = free; // 设置JVM空闲内存
    }

    public double getUsed()
    {
        return Arith.div(total - free, (1024 * 1024), 2); // 计算已用内存(MB)
    }

    public double getUsage()
    {
        return Arith.mul(Arith.div(total - free, total, 4), 100); // 计算JVM内存使用率百分比
    }

    /**
     * 获取JDK名称
     */
    public String getName()
    {
        return ManagementFactory.getRuntimeMXBean().getVmName(); // 获取JVM运行时名称
    }

    // getter/setter方法
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getHome() { return home; }
    public void setHome(String home) { this.home = home; }

    /**
     * JDK启动时间
     */
    public String getStartTime()
    {
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, DateUtils.getServerStartDate()); // 格式化JVM启动时间
    }

    /**
     * JDK运行时间
     */
    public String getRunTime()
    {
        return DateUtils.timeDistance(DateUtils.getNowDate(), DateUtils.getServerStartDate()); // 计算JVM运行时长
    }

    /**
     * 运行参数
     */
    public String getInputArgs()
    {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().toString(); // 获取JVM启动参数列表
    }
}
