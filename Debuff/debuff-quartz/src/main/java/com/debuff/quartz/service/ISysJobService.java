// 包声明
package com.debuff.quartz.service;

// 导入所需类
import java.util.List;
import org.quartz.SchedulerException;
import com.debuff.common.exception.job.TaskException;
import com.debuff.quartz.domain.SysJob;

/**
 * 定时任务调度信息信息 服务层
 * 
 * @author ruoyi
 */
public interface ISysJobService
{
    /**
     * 获取quartz调度器的计划任务
     * 
     * @param job 调度信息
     * @return 调度任务集合
     */
    public List<SysJob> selectJobList(SysJob job); // 条件查询调度任务列表

    /**
     * 通过调度任务ID查询调度信息
     * 
     * @param jobId 调度任务ID
     * @return 调度任务对象信息
     */
    public SysJob selectJobById(Long jobId); // 根据ID查询调度任务

    /**
     * 暂停任务
     * 
     * @param job 调度信息
     * @return 结果
     */
    public int pauseJob(SysJob job) throws SchedulerException; // 暂停定时任务

    /**
     * 恢复任务
     * 
     * @param job 调度信息
     * @return 结果
     */
    public int resumeJob(SysJob job) throws SchedulerException; // 恢复定时任务

    /**
     * 删除任务后，所对应的trigger也将被删除
     * 
     * @param job 调度信息
     * @return 结果
     */
    public int deleteJob(SysJob job) throws SchedulerException; // 删除定时任务

    /**
     * 批量删除调度信息
     * 
     * @param jobIds 需要删除的任务ID
     * @return 结果
     */
    public void deleteJobByIds(Long[] jobIds) throws SchedulerException; // 批量删除定时任务

    /**
     * 任务调度状态修改
     * 
     * @param job 调度信息
     * @return 结果
     */
    public int changeStatus(SysJob job) throws SchedulerException; // 修改任务状态（暂停/恢复）

    /**
     * 立即运行任务
     * 
     * @param job 调度信息
     * @return 结果
     */
    public boolean run(SysJob job) throws SchedulerException; // 立即执行一次任务

    /**
     * 新增任务
     * 
     * @param job 调度信息
     * @return 结果
     */
    public int insertJob(SysJob job) throws SchedulerException, TaskException; // 新增定时任务

    /**
     * 更新任务
     * 
     * @param job 调度信息
     * @return 结果
     */
    public int updateJob(SysJob job) throws SchedulerException, TaskException; // 更新定时任务

    /**
     * 校验cron表达式是否有效
     * 
     * @param cronExpression 表达式
     * @return 结果
     */
    public boolean checkCronExpressionIsValid(String cronExpression); // 校验cron表达式有效性
}
