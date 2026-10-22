// 包声明
package com.debuff.quartz.util;

// 导入所需类
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.debuff.common.constant.Constants;
import com.debuff.common.constant.ScheduleConstants;
import com.debuff.common.utils.ExceptionUtil;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.bean.BeanUtils;
import com.debuff.common.utils.spring.SpringUtils;
import com.debuff.quartz.domain.SysJob;
import com.debuff.quartz.domain.SysJobLog;
import com.debuff.quartz.service.ISysJobLogService;

/**
 * 抽象quartz调用
 *
 * @author ruoyi
 */
public abstract class AbstractQuartzJob implements Job // 实现Quartz的Job接口
{
    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class); // 日志对象

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>(); // 线程本地变量，记录任务开始时间

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException // Quartz任务执行入口
    {
        SysJob sysJob = new SysJob();
        BeanUtils.copyBeanProp(sysJob, context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES)); // 从上下文获取任务信息
        try
        {
            before(context, sysJob); // 执行前处理（记录开始时间）
            if (sysJob != null)
            {
                doExecute(context, sysJob); // 执行子类实现的具体任务逻辑
            }
            after(context, sysJob, null); // 执行后处理（记录日志）
        }
        catch (Exception e) // 捕获任务执行异常
        {
            log.error("任务执行异常  - ：", e); // 记录异常日志
            after(context, sysJob, e); // 执行后处理（记录异常信息）
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob) // 执行前记录开始时间
    {
        threadLocal.set(new Date()); // 将当前时间存入线程本地变量
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) // 执行后记录任务日志
    {
        Date startTime = threadLocal.get(); // 获取任务开始时间
        threadLocal.remove(); // 清除线程本地变量，防止内存泄漏

        final SysJobLog sysJobLog = new SysJobLog(); // 创建任务日志对象
        sysJobLog.setJobName(sysJob.getJobName()); // 设置任务名称
        sysJobLog.setJobGroup(sysJob.getJobGroup()); // 设置任务组名
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget()); // 设置调用目标
        sysJobLog.setStartTime(startTime); // 设置开始时间
        sysJobLog.setStopTime(new Date()); // 设置结束时间
        long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime(); // 计算执行耗时（毫秒）
        sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒"); // 设置日志消息
        if (e != null) // 如果有异常
        {
            sysJobLog.setStatus(Constants.FAIL); // 状态设为失败
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000); // 截取异常信息（最多2000字符）
            sysJobLog.setExceptionInfo(errorMsg); // 设置异常信息
        }
        else // 无异常
        {
            sysJobLog.setStatus(Constants.SUCCESS); // 状态设为成功
        }

        // 写入数据库当中
        SpringUtils.getBean(ISysJobLogService.class).addJobLog(sysJobLog); // 通过Spring获取服务并保存日志
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception; // 抽象方法，子类实现具体任务逻辑
}
