// 包声明
package com.debuff.quartz.domain;

// 导入所需类
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
import com.debuff.common.constant.ScheduleConstants;
import com.debuff.common.core.domain.BaseEntity;
import com.debuff.common.utils.StringUtils;
import com.debuff.quartz.util.CronUtils;

/**
 * 定时任务调度表 sys_job
 * 
 * @author ruoyi
 */
public class SysJob extends BaseEntity // 定时任务实体类，对应sys_job表
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 任务ID */
    @Excel(name = "任务序号", cellType = ColumnType.NUMERIC) // Excel导出列配置
    private Long jobId;

    /** 任务名称 */
    @Excel(name = "任务名称") // Excel导出列配置
    private String jobName;

    /** 任务组名 */
    @Excel(name = "任务组名") // Excel导出列配置
    private String jobGroup;

    /** 调用目标字符串 */
    @Excel(name = "调用目标字符串") // Excel导出列配置
    private String invokeTarget;

    /** cron执行表达式 */
    @Excel(name = "执行表达式 ") // Excel导出列配置
    private String cronExpression;

    /** cron计划策略 */
    @Excel(name = "计划策略 ", readConverterExp = "0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行") // Excel导出带转换
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT; // 默认策略

    /** 是否并发执行（0允许 1禁止） */
    @Excel(name = "并发执行", readConverterExp = "0=允许,1=禁止") // Excel导出带转换
    private String concurrent;

    /** 任务状态（0正常 1暂停） */
    @Excel(name = "任务状态", readConverterExp = "0=正常,1=暂停") // Excel导出带转换
    private String status;

    // getter/setter方法
    public Long getJobId()
    {
        return jobId;
    }

    public void setJobId(Long jobId)
    {
        this.jobId = jobId;
    }

    @NotBlank(message = "任务名称不能为空") // 非空校验
    @Size(min = 0, max = 64, message = "任务名称不能超过64个字符") // 长度校验
    public String getJobName()
    {
        return jobName;
    }

    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getJobGroup()
    {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup)
    {
        this.jobGroup = jobGroup;
    }

    @NotBlank(message = "调用目标字符串不能为空") // 非空校验
    @Size(min = 0, max = 500, message = "调用目标字符串长度不能超过500个字符") // 长度校验
    public String getInvokeTarget()
    {
        return invokeTarget;
    }

    public void setInvokeTarget(String invokeTarget)
    {
        this.invokeTarget = invokeTarget;
    }

    @NotBlank(message = "Cron执行表达式不能为空") // 非空校验
    @Size(min = 0, max = 255, message = "Cron执行表达式不能超过255个字符") // 长度校验
    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON日期格式化
    public Date getNextValidTime() // 获取下次有效执行时间
    {
        if (StringUtils.isNotEmpty(cronExpression)) // cron表达式不为空
        {
            return CronUtils.getNextExecution(cronExpression); // 计算下次执行时间
        }
        return null;
    }

    public String getMisfirePolicy()
    {
        return misfirePolicy;
    }

    public void setMisfirePolicy(String misfirePolicy)
    {
        this.misfirePolicy = misfirePolicy;
    }

    public String getConcurrent()
    {
        return concurrent;
    }

    public void setConcurrent(String concurrent)
    {
        this.concurrent = concurrent;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("jobId", getJobId()) // 任务ID
            .append("jobName", getJobName()) // 任务名称
            .append("jobGroup", getJobGroup()) // 任务组名
            .append("cronExpression", getCronExpression()) // cron表达式
            .append("nextValidTime", getNextValidTime()) // 下次执行时间
            .append("misfirePolicy", getMisfirePolicy()) // misfire策略
            .append("concurrent", getConcurrent()) // 并发设置
            .append("status", getStatus()) // 任务状态
            .append("createBy", getCreateBy()) // 创建者
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateBy", getUpdateBy()) // 更新者
            .append("updateTime", getUpdateTime()) // 更新时间
            .append("remark", getRemark()) // 备注
            .toString();
    }
}
