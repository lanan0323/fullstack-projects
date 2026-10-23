// 包声明
package com.debuff.quartz.util;

// 导入所需类
import org.quartz.JobExecutionContext;
import com.debuff.quartz.domain.SysJob;

/**
 * 定时任务处理（允许并发执行）
 * 
 * @author ruoyi
 *
 */
public class QuartzJobExecution extends AbstractQuartzJob // 允许并发的定时任务实现
{
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception // 执行任务
    {
        JobInvokeUtil.invokeMethod(sysJob); // 调用任务执行工具
    }
}
