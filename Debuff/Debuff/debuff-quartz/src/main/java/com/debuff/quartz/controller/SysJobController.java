// 包声明
package com.debuff.quartz.controller;

// 导入所需类
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Log;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.exception.job.TaskException;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.quartz.domain.SysJob;
import com.debuff.quartz.service.ISysJobService;
import com.debuff.quartz.util.CronUtils;
import com.debuff.quartz.util.ScheduleUtils;

/**
 * 调度任务信息操作处理
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/monitor/job") // 请求路径前缀
public class SysJobController extends BaseController
{
    @Autowired // 自动注入定时任务服务
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:list')") // 权限校验：定时任务列表
    @GetMapping("/list") // GET请求
    public TableDataInfo list(SysJob sysJob)
    {
        startPage(); // 设置分页参数
        List<SysJob> list = jobService.selectJobList(sysJob); // 查询定时任务列表
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 导出定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:export')") // 权限校验：定时任务导出
    @Log(title = "定时任务", businessType = BusinessType.EXPORT) // 记录操作日志
    @PostMapping("/export") // POST导出请求
    public void export(HttpServletResponse response, SysJob sysJob)
    {
        List<SysJob> list = jobService.selectJobList(sysJob); // 查询定时任务列表
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class); // 创建Excel工具
        util.exportExcel(response, list, "定时任务"); // 导出Excel
    }

    /**
     * 获取定时任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:query')") // 权限校验：定时任务查询
    @GetMapping(value = "/{jobId}") // GET请求，路径带ID
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId)
    {
        return success(jobService.selectJobById(jobId)); // 根据ID查询任务详情
    }

    /**
     * 新增定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:add')") // 权限校验：定时任务新增
    @Log(title = "定时任务", businessType = BusinessType.INSERT) // 记录操作日志
    @PostMapping // POST请求
    public AjaxResult add(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression())) // 校验cron表达式
        {
            return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) // 禁止RMI调用
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS })) // 禁止LDAP调用
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS })) // 禁止HTTP调用
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) // 禁止违规字符串
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) // 白名单校验
        {
            return error("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setCreateBy(getUsername()); // 设置创建者
        return toAjax(jobService.insertJob(job)); // 插入任务
    }

    /**
     * 修改定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')") // 权限校验：定时任务编辑
    @Log(title = "定时任务", businessType = BusinessType.UPDATE) // 记录操作日志
    @PutMapping // PUT请求
    public AjaxResult edit(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression())) // 校验cron表达式
        {
            return error("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI)) // 禁止RMI调用
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS })) // 禁止LDAP调用
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS })) // 禁止HTTP调用
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR)) // 禁止违规字符串
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget())) // 白名单校验
        {
            return error("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setUpdateBy(getUsername()); // 设置修改者
        return toAjax(jobService.updateJob(job)); // 更新任务
    }

    /**
     * 定时任务状态修改
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')") // 权限校验：状态修改
    @Log(title = "定时任务", businessType = BusinessType.UPDATE) // 记录操作日志
    @PutMapping("/changeStatus") // PUT请求
    public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getJobId()); // 查询完整任务信息
        newJob.setStatus(job.getStatus()); // 设置新状态
        return toAjax(jobService.changeStatus(newJob)); // 修改状态
    }

    /**
     * 定时任务立即执行一次
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')") // 权限校验：执行任务
    @Log(title = "定时任务", businessType = BusinessType.UPDATE) // 记录操作日志
    @PutMapping("/run") // PUT请求
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException
    {
        boolean result = jobService.run(job); // 立即执行任务
        return result ? success() : error("任务不存在或已过期！");
    }

    /**
     * 删除定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')") // 权限校验：删除任务
    @Log(title = "定时任务", businessType = BusinessType.DELETE) // 记录操作日志
    @DeleteMapping("/{jobIds}") // DELETE请求，支持批量
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException
    {
        jobService.deleteJobByIds(jobIds); // 批量删除任务
        return success();
    }
}
