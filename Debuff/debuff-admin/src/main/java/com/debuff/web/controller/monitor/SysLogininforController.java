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
import com.debuff.framework.web.service.SysPasswordService;
import com.debuff.system.domain.SysLogininfor;
import com.debuff.system.service.ISysLogininforService;

/**
 * 系统访问记录
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/monitor/logininfor") // 请求路径前缀
public class SysLogininforController extends BaseController
{
    @Autowired // 自动注入登录日志服务
    private ISysLogininforService logininforService;

    @Autowired // 自动注入密码服务（用于账户解锁）
    private SysPasswordService passwordService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')") // 权限校验：登录日志列表
    @GetMapping("/list") // GET请求
    public TableDataInfo list(SysLogininfor logininfor)
    {
        startPage(); // 设置分页参数
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor); // 查询登录日志列表
        return getDataTable(list); // 返回分页数据
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT) // 记录操作日志
    @PreAuthorize("@ss.hasPermi('monitor:logininfor:export')") // 权限校验：登录日志导出
    @PostMapping("/export") // POST导出请求
    public void export(HttpServletResponse response, SysLogininfor logininfor)
    {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor); // 查询登录日志列表
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class); // 创建Excel工具
        util.exportExcel(response, list, "登录日志"); // 导出Excel
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')") // 权限校验：登录日志删除
    @Log(title = "登录日志", businessType = BusinessType.DELETE) // 记录操作日志
    @DeleteMapping("/{infoIds}") // DELETE请求，支持批量
    public AjaxResult remove(@PathVariable Long[] infoIds)
    {
        return toAjax(logininforService.deleteLogininforByIds(infoIds)); // 批量删除登录日志
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')") // 权限校验：登录日志清空
    @Log(title = "登录日志", businessType = BusinessType.CLEAN) // 记录操作日志
    @DeleteMapping("/clean") // DELETE清空请求
    public AjaxResult clean()
    {
        logininforService.cleanLogininfor(); // 清空所有登录日志
        return success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')") // 权限校验：账户解锁
    @Log(title = "账户解锁", businessType = BusinessType.OTHER) // 记录操作日志
    @GetMapping("/unlock/{userName}") // GET解锁请求
    public AjaxResult unlock(@PathVariable("userName") String userName)
    {
        passwordService.clearLoginRecordCache(userName); // 清除用户登录失败缓存，解锁账户
        return success();
    }
}
