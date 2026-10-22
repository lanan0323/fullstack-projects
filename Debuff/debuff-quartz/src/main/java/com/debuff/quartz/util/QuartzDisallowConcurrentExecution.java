// 包声明
package com.debuff.quartz.util;

// 导入所需类
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import com.debuff.quartz.domain.SysJob;

/**
 * 定时任务处理（禁止并发执行）
 * 
 * @author ruoyi
 *
 */
@DisallowConcurrentExecution // Quartz注解：禁止同一任务并发执行
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob // 禁止并发的定时任务实现
{
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception // 执行任务
    {
        JobInvokeUtil.invokeMethod(sysJob); // 调用任务执行工具
    }
}
