// 包声明
package com.debuff.web.controller.monitor;

// 导入所需类
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.system.domain.SysOperLog;
import com.debuff.system.service.ISysOperLogService;

/**
 * 操作日志记录
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/monitor/operlog") // 请求路径前缀
public class SysOperlogController extends BaseController
{
    @Autowired // 自动注入操作日志服务
    private ISysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')") // 权限校验：操作日志列表
    @GetMapping("/list") // GET请求
    public TableDataInfo list(SysOperLog operLog)
    {
        startPage(); // 设置分页参数
        List<SysOperLog> list = operLogService.selectOperLogList(operLog); // 查询操作日志列表
        return getDataTable(list); // 返回分页数据
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT) // 记录操作日志
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')") // 权限校验：操作日志导出
    @PostMapping("/export") // POST导出请求
    public void export(HttpServletResponse response, SysOperLog operLog)
    {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog); // 查询操作日志列表
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class); // 创建Excel工具
        util.exportExcel(response, list, "操作日志"); // 导出Excel
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE) // 记录操作日志
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')") // 权限校验：操作日志删除
    @DeleteMapping("/{operIds}") // DELETE请求，支持批量
    public AjaxResult remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds)); // 批量删除操作日志
    }

    @Log(title = "操作日志", businessType = BusinessType.CLEAN) // 记录操作日志
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')") // 权限校验：操作日志清空
    @DeleteMapping("/clean") // DELETE清空请求
    public AjaxResult clean()
    {
        operLogService.cleanOperLog(); // 清空所有操作日志
        return success();
    }
}
