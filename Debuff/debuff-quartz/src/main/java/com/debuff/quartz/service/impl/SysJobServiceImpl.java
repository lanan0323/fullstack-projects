// 包声明
package com.debuff.quartz.service.impl;

// 导入所需类
import java.util.List;
import javax.annotation.PostConstruct;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.debuff.common.constant.ScheduleConstants;
import com.debuff.common.exception.job.TaskException;
import com.debuff.quartz.domain.SysJob;
import com.debuff.quartz.mapper.SysJobMapper;
import com.debuff.quartz.service.ISysJobService;
import com.debuff.quartz.util.CronUtils;
import com.debuff.quartz.util.ScheduleUtils;

/**
 * 定时任务调度信息 服务层
 * 
 * @author ruoyi
 */
@Service // 标记为Spring服务
public class SysJobServiceImpl implements ISysJobService
{
    @Autowired // 自动注入Quartz调度器
    private Scheduler scheduler;

    @Autowired // 自动注入任务Mapper
    private SysJobMapper jobMapper;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct // 项目启动后自动执行
    public void init() throws SchedulerException, TaskException
    {
        scheduler.clear(); // 清空调度器中已有的任务
        List<SysJob> jobList = jobMapper.selectJobAll(); // 从数据库查询所有定时任务
        for (SysJob job : jobList) // 遍历所有任务
        {
            ScheduleUtils.createScheduleJob(scheduler, job); // 将任务注册到Quartz调度器
        }
    }

    /**
     * 获取quartz调度器的计划任务列表
     * 
     * @param job 调度信息
     * @return
     */
    @Override
    public List<SysJob> selectJobList(SysJob job)
    {
        return jobMapper.selectJobList(job); // 调用Mapper查询任务列表
    }

    /**
     * 通过调度任务ID查询调度信息
     * 
     * @param jobId 调度任务ID
     * @return 调度任务对象信息
     */
    @Override
    public SysJob selectJobById(Long jobId)
    {
        return jobMapper.selectJobById(jobId); // 根据ID查询任务
    }

    /**
     * 暂停任务
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public int pauseJob(SysJob job) throws SchedulerException
    {
        Long jobId = job.getJobId(); // 获取任务ID
        String jobGroup = job.getJobGroup(); // 获取任务组名
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue()); // 设置状态为暂停
        int rows = jobMapper.updateJob(job); // 更新数据库中的任务状态
        if (rows > 0) // 数据库更新成功
        {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup)); // 暂停Quartz调度器中的任务
        }
        return rows;
    }

    /**
     * 恢复任务
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public int resumeJob(SysJob job) throws SchedulerException
    {
        Long jobId = job.getJobId(); // 获取任务ID
        String jobGroup = job.getJobGroup(); // 获取任务组名
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue()); // 设置状态为正常
        int rows = jobMapper.updateJob(job); // 更新数据库中的任务状态
        if (rows > 0) // 数据库更新成功
        {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup)); // 恢复Quartz调度器中的任务
        }
        return rows;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public int deleteJob(SysJob job) throws SchedulerException
    {
        Long jobId = job.getJobId(); // 获取任务ID
        String jobGroup = job.getJobGroup(); // 获取任务组名
        int rows = jobMapper.deleteJobById(jobId); // 从数据库删除任务
        if (rows > 0) // 数据库删除成功
        {
            scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup)); // 从Quartz调度器删除任务
        }
        return rows;
    }

    /**
     * 批量删除调度信息
     * 
     * @param jobIds 需要删除的任务ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public void deleteJobByIds(Long[] jobIds) throws SchedulerException
    {
        for (Long jobId : jobIds) // 遍历所有要删除的任务ID
        {
            SysJob job = jobMapper.selectJobById(jobId); // 查询任务信息
            deleteJob(job); // 逐个删除
        }
    }

    /**
     * 任务调度状态修改
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public int changeStatus(SysJob job) throws SchedulerException
    {
        int rows = 0;
        String status = job.getStatus(); // 获取目标状态
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) // 如果目标是正常状态
        {
            rows = resumeJob(job); // 恢复任务
        }
        else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) // 如果目标是暂停状态
        {
            rows = pauseJob(job); // 暂停任务
        }
        return rows;
    }

    /**
     * 立即运行任务
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public boolean run(SysJob job) throws SchedulerException
    {
        boolean result = false;
        Long jobId = job.getJobId(); // 获取任务ID
        String jobGroup = job.getJobGroup(); // 获取任务组名
        SysJob properties = selectJobById(job.getJobId()); // 查询完整的任务信息
        // 参数
        JobDataMap dataMap = new JobDataMap(); // 创建任务数据Map
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties); // 放入任务属性
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup); // 获取任务Key
        if (scheduler.checkExists(jobKey)) // 检查调度器中是否存在该任务
        {
            result = true;
            scheduler.triggerJob(jobKey, dataMap); // 立即触发执行任务
        }
        return result;
    }

    /**
     * 新增任务
     * 
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public int insertJob(SysJob job) throws SchedulerException, TaskException
    {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue()); // 新增任务默认暂停状态
        int rows = jobMapper.insertJob(job); // 插入数据库
        if (rows > 0) // 插入成功
        {
            ScheduleUtils.createScheduleJob(scheduler, job); // 注册到Quartz调度器
        }
        return rows;
    }

    /**
     * 更新任务的时间表达式
     * 
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务管理
    public int updateJob(SysJob job) throws SchedulerException, TaskException
    {
        SysJob properties = selectJobById(job.getJobId()); // 查询旧的任务信息
        int rows = jobMapper.updateJob(job); // 更新数据库
        if (rows > 0) // 更新成功
        {
            updateSchedulerJob(job, properties.getJobGroup()); // 同步更新Quartz调度器中的任务
        }
        return rows;
    }

    /**
     * 更新任务
     * 
     * @param job 任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException, TaskException
    {
        Long jobId = job.getJobId(); // 获取任务ID
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup); // 获取任务Key
        if (scheduler.checkExists(jobKey)) // 调度器中存在该任务
        {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey); // 先删除旧任务
        }
        ScheduleUtils.createScheduleJob(scheduler, job); // 重新创建任务
    }

    /**
     * 校验cron表达式是否有效
     * 
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression)
    {
        return CronUtils.isValid(cronExpression); // 调用工具类校验cron表达式
    }
}
