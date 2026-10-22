// 包声明
package com.debuff.framework.web.domain;

// 导入所需类
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import com.debuff.common.utils.Arith;
import com.debuff.common.utils.ip.IpUtils;
import com.debuff.framework.web.domain.server.Cpu;
import com.debuff.framework.web.domain.server.Jvm;
import com.debuff.framework.web.domain.server.Mem;
import com.debuff.framework.web.domain.server.Sys;
import com.debuff.framework.web.domain.server.SysFile;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

/**
 * 服务器相关信息
 * 
 * @author ruoyi
 */
public class Server
{
    private static final int OSHI_WAIT_SECOND = 1000; // OSHI采集CPU使用率的等待时间(毫秒)
    
    /**
     * CPU相关信息
     */
    private Cpu cpu = new Cpu(); // CPU信息对象

    /**
     * 內存相关信息
     */
    private Mem mem = new Mem(); // 内存信息对象

    /**
     * JVM相关信息
     */
    private Jvm jvm = new Jvm(); // JVM信息对象

    /**
     * 服务器相关信息
     */
    private Sys sys = new Sys(); // 服务器系统信息对象

    /**
     * 磁盘相关信息
     */
    private List<SysFile> sysFiles = new LinkedList<SysFile>(); // 磁盘分区信息列表

    // getter/setter方法
    public Cpu getCpu() { return cpu; }
    public void setCpu(Cpu cpu) { this.cpu = cpu; }
    public Mem getMem() { return mem; }
    public void setMem(Mem mem) { this.mem = mem; }
    public Jvm getJvm() { return jvm; }
    public void setJvm(Jvm jvm) { this.jvm = jvm; }
    public Sys getSys() { return sys; }
    public void setSys(Sys sys) { this.sys = sys; }
    public List<SysFile> getSysFiles() { return sysFiles; }
    public void setSysFiles(List<SysFile> sysFiles) { this.sysFiles = sysFiles; }

    /**
     * 采集服务器所有硬件和系统信息
     */
    public void copyTo() throws Exception
    {
        SystemInfo si = new SystemInfo(); // 创建OSHI系统信息对象
        HardwareAbstractionLayer hal = si.getHardware(); // 获取硬件抽象层

        setCpuInfo(hal.getProcessor()); // 采集CPU信息

        setMemInfo(hal.getMemory()); // 采集内存信息

        setSysInfo(); // 采集服务器系统信息

        setJvmInfo(); // 采集JVM信息

        setSysFiles(si.getOperatingSystem()); // 采集磁盘信息
    }

    /**
     * 设置CPU信息
     */
    private void setCpuInfo(CentralProcessor processor)
    {
        // CPU信息
        long[] prevTicks = processor.getSystemCpuLoadTicks(); // 获取当前CPU时间片快照
        Util.sleep(OSHI_WAIT_SECOND); // 等待1秒后再次采样
        long[] ticks = processor.getSystemCpuLoadTicks(); // 获取第二次CPU时间片快照
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()]; // 计算nice优先级时间差
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()]; // 计算硬中断时间差
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()]; // 计算软中断时间差
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()]; // 计算虚拟化偷取时间差
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()]; // 计算系统内核时间差
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()]; // 计算用户空间时间差
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()]; // 计算IO等待时间差
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()]; // 计算空闲时间差
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal; // 计算CPU总时间
        cpu.setCpuNum(processor.getLogicalProcessorCount()); // 设置逻辑处理器核心数
        cpu.setTotal(totalCpu); // 设置CPU总时间
        cpu.setSys(cSys); // 设置系统使用时间
        cpu.setUsed(user); // 设置用户使用时间
        cpu.setWait(iowait); // 设置IO等待时间
        cpu.setFree(idle); // 设置空闲时间
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory)
    {
        mem.setTotal(memory.getTotal()); // 设置总内存大小(字节)
        mem.setUsed(memory.getTotal() - memory.getAvailable()); // 设置已用内存(总内存-可用内存)
        mem.setFree(memory.getAvailable()); // 设置可用内存
    }

    /**
     * 设置服务器信息
     */
    private void setSysInfo()
    {
        Properties props = System.getProperties(); // 获取系统属性
        sys.setComputerName(IpUtils.getHostName()); // 设置主机名
        sys.setComputerIp(IpUtils.getHostIp()); // 设置主机IP
        sys.setOsName(props.getProperty("os.name")); // 设置操作系统名称
        sys.setOsArch(props.getProperty("os.arch")); // 设置系统架构
        sys.setUserDir(props.getProperty("user.dir")); // 设置项目工作目录
    }

    /**
     * 设置Java虚拟机
     */
    private void setJvmInfo() throws UnknownHostException
    {
        Properties props = System.getProperties(); // 获取系统属性
        jvm.setTotal(Runtime.getRuntime().totalMemory()); // 设置JVM总内存
        jvm.setMax(Runtime.getRuntime().maxMemory()); // 设置JVM最大可用内存
        jvm.setFree(Runtime.getRuntime().freeMemory()); // 设置JVM空闲内存
        jvm.setVersion(props.getProperty("java.version")); // 设置JDK版本
        jvm.setHome(props.getProperty("java.home")); // 设置JDK安装路径
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles(OperatingSystem os)
    {
        FileSystem fileSystem = os.getFileSystem(); // 获取文件系统对象
        List<OSFileStore> fsArray = fileSystem.getFileStores(); // 获取所有磁盘分区
        for (OSFileStore fs : fsArray) // 遍历每个磁盘分区
        {
            long free = fs.getUsableSpace(); // 获取分区可用空间
            long total = fs.getTotalSpace(); // 获取分区总空间
            long used = total - free; // 计算已用空间
            SysFile sysFile = new SysFile(); // 创建磁盘信息对象
            sysFile.setDirName(fs.getMount()); // 设置挂载路径
            sysFile.setSysTypeName(fs.getType()); // 设置文件系统类型
            sysFile.setTypeName(fs.getName()); // 设置分区名称
            sysFile.setTotal(convertFileSize(total)); // 设置总大小(格式化)
            sysFile.setFree(convertFileSize(free)); // 设置剩余大小(格式化)
            sysFile.setUsed(convertFileSize(used)); // 设置已用大小(格式化)
            sysFile.setUsage(Arith.mul(Arith.div(used, total, 4), 100)); // 设置使用率百分比
            sysFiles.add(sysFile); // 添加到磁盘信息列表
        }
    }

    /**
     * 字节转换
     * 
     * @param size 字节大小
     * @return 转换后值
     */
    public String convertFileSize(long size)
    {
        long kb = 1024; // 1KB = 1024字节
        long mb = kb * 1024; // 1MB = 1024KB
        long gb = mb * 1024; // 1GB = 1024MB
        if (size >= gb) // 大于等于1GB
        {
            return String.format("%.1f GB", (float) size / gb); // 格式化为GB
        }
        else if (size >= mb) // 大于等于1MB
        {
            float f = (float) size / mb; // 转换为MB数值
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f); // 格式化，大于100不保留小数
        }
        else if (size >= kb) // 大于等于1KB
        {
            float f = (float) size / kb; // 转换为KB数值
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f); // 格式化，大于100不保留小数
        }
        else // 小于1KB
        {
            return String.format("%d B", size); // 直接显示字节数
        }
    }
}
