// 包声明
package com.debuff.framework.web.domain.server;

// 导入所需类
import com.debuff.common.utils.Arith;

/**
 * 內存相关信息
 * 
 * @author ruoyi
 */
public class Mem
{
    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    public double getTotal()
    {
        return Arith.div(total, (1024 * 1024 * 1024), 2); // 转换为GB，保留2位小数
    }

    public void setTotal(long total)
    {
        this.total = total; // 设置总内存(字节)
    }

    public double getUsed()
    {
        return Arith.div(used, (1024 * 1024 * 1024), 2); // 转换为GB，保留2位小数
    }

    public void setUsed(long used)
    {
        this.used = used; // 设置已用内存(字节)
    }

    public double getFree()
    {
        return Arith.div(free, (1024 * 1024 * 1024), 2); // 转换为GB，保留2位小数
    }

    public void setFree(long free)
    {
        this.free = free; // 设置剩余内存(字节)
    }

    public double getUsage()
    {
        return Arith.mul(Arith.div(used, total, 4), 100); // 计算内存使用率百分比
    }
}
