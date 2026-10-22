// 包声明
package com.debuff.quartz.mapper;

// 导入所需类
import java.util.List;
import com.debuff.quartz.domain.SysJobLog;

/**
 * 调度任务日志信息 数据层
 * 
 * @author ruoyi
 */
public interface SysJobLogMapper
{
    /**
     * 获取quartz调度器日志的计划任务
     * 
     * @param jobLog 调度日志信息
     * @return 调度任务日志集合
     */
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog); // 条件查询调度日志列表

    /**
     * 查询所有调度任务日志
     *
     * @return 调度任务日志列表
     */
    public List<SysJobLog> selectJobLogAll(); // 查询全部调度日志

    /**
     * 通过调度任务日志ID查询调度信息
     * 
     * @param jobLogId 调度任务日志ID
     * @return 调度任务日志对象信息
     */
    public SysJobLog selectJobLogById(Long jobLogId); // 根据ID查询调度日志

    /**
     * 新增任务日志
     * 
     * @param jobLog 调度日志信息
     * @return 结果
     */
    public int insertJobLog(SysJobLog jobLog); // 插入调度日志

    /**
     * 批量删除调度日志信息
     * 
     * @param logIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteJobLogByIds(Long[] logIds); // 批量删除调度日志

    /**
     * 删除任务日志
     * 
     * @param jobId 调度日志ID
     * @return 结果
     */
    public int deleteJobLogById(Long jobId); // 删除单条调度日志

    /**
     * 清空任务日志
     */
    public void cleanJobLog(); // 清空所有调度日志
}
