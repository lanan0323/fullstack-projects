// 包声明
package com.debuff.quartz.util;

// 导入所需类
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import com.debuff.common.constant.Constants;
import com.debuff.common.constant.ScheduleConstants;
import com.debuff.common.exception.job.TaskException;
import com.debuff.common.exception.job.TaskException.Code;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.spring.SpringUtils;
import com.debuff.quartz.domain.SysJob;

/**
 * 定时任务工具类
 * 
 * @author ruoyi
 *
 */
public class ScheduleUtils // 定时任务调度工具类
{
    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(SysJob sysJob) // 根据并发策略获取任务执行类
    {
        boolean isConcurrent = "0".equals(sysJob.getConcurrent()); // 是否允许并发（0=允许）
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class; // 返回对应的任务类
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) // 构建触发器Key
    {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup); // 由任务ID和组名组成
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) // 构建任务Key
    {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup); // 由任务ID和组名组成
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException, TaskException // 创建并注册定时任务到Quartz
    {
        Class<? extends Job> jobClass = getQuartzJobClass(job); // 获取任务执行类
        // 构建job信息
        Long jobId = job.getJobId(); // 任务ID
        String jobGroup = job.getJobGroup(); // 任务组名
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build(); // 构建JobDetail

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression()); // 设置cron表达式
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder); // 设置misfire策略

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build(); // 构建CronTrigger

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job); // 将任务信息放入数据Map

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) // 调度器中已存在该任务
        {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup)); // 先删除旧任务
        }

        // 判断任务是否过期
        if (StringUtils.isNotNull(CronUtils.getNextExecution(job.getCronExpression()))) // cron表达式有下次执行时间（未过期）
        {
            // 执行调度任务
            scheduler.scheduleJob(jobDetail, trigger); // 注册任务到调度器
        }

        // 暂停任务
        if (job.getStatus().equals(ScheduleConstants.Status.PAUSE.getValue())) // 任务状态为暂停
        {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup)); // 暂停任务
        }
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder cb) // 设置任务misfire处理策略
            throws TaskException
    {
        switch (job.getMisfirePolicy()) // 根据misfire策略类型处理
        {
            case ScheduleConstants.MISFIRE_DEFAULT: // 默认策略
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES: // 忽略misfire，立即执行所有错过的
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED: // 执行一次并继续
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING: // 什么都不做，等待下次
                return cb.withMisfireHandlingInstructionDoNothing();
            default: // 未知策略
                throw new TaskException("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks", Code.CONFIG_ERROR);
        }
    }

    /**
     * 检查包名是否为白名单配置
     * 
     * @param invokeTarget 目标字符串
     * @return 结果
     */
    public static boolean whiteList(String invokeTarget) // 校验调用目标是否在白名单内
    {
        String packageName = StringUtils.substringBefore(invokeTarget, "("); // 获取包名/类名部分
        int count = StringUtils.countMatches(packageName, "."); // 计算点数
        if (count > 1) // 完整类名
        {
            return StringUtils.containsAnyIgnoreCase(invokeTarget, Constants.JOB_WHITELIST_STR); // 检查是否在白名单中
        }
        Object obj = SpringUtils.getBean(StringUtils.split(invokeTarget, ".")[0]); // 获取Spring Bean
        String beanPackageName = obj.getClass().getPackage().getName(); // 获取Bean所在包名
        return StringUtils.containsAnyIgnoreCase(beanPackageName, Constants.JOB_WHITELIST_STR) // 包名在白名单中
                && !StringUtils.containsAnyIgnoreCase(beanPackageName, Constants.JOB_ERROR_STR); // 且不在黑名单中
    }
}
