// 包声明
package com.debuff.framework.web.domain.server;

// 导入所需类
import com.debuff.common.utils.Arith;

/**
 * CPU相关信息
 * 
 * @author ruoyi
 */
public class Cpu
{
    /**
     * 核心数
     */
    private int cpuNum;

    /**
     * CPU总的使用率
     */
    private double total;

    /**
     * CPU系统使用率
     */
    private double sys;

    /**
     * CPU用户使用率
     */
    private double used;

    /**
     * CPU当前等待率
     */
    private double wait;

    /**
     * CPU当前空闲率
     */
    private double free;

    // getter/setter方法
    public int getCpuNum() { return cpuNum; }
    public void setCpuNum(int cpuNum) { this.cpuNum = cpuNum; }

    public double getTotal()
    {
        return Arith.round(Arith.mul(total, 100), 2); // 返回CPU总使用率(百分比，保留2位小数)
    }

    public void setTotal(double total) { this.total = total; }

    public double getSys()
    {
        return Arith.round(Arith.mul(sys / total, 100), 2); // 返回系统使用率百分比
    }

    public void setSys(double sys) { this.sys = sys; }

    public double getUsed()
    {
        return Arith.round(Arith.mul(used / total, 100), 2); // 返回用户使用率百分比
    }

    public void setUsed(double used) { this.used = used; }

    public double getWait()
    {
        return Arith.round(Arith.mul(wait / total, 100), 2); // 返回等待率百分比
    }

    public void setWait(double wait) { this.wait = wait; }

    public double getFree()
    {
        return Arith.round(Arith.mul(free / total, 100), 2); // 返回空闲率百分比
    }

    public void setFree(double free) { this.free = free; }
}
