// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Anonymous;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.enums.BusinessType;
import com.debuff.trade.domain.Announcement;
import com.debuff.trade.service.IAnnouncementService;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.common.core.page.TableDataInfo;

/**
 * 系统公告Controller
 *
 * @author debuff
 * @date 2026-08-25
 */
@RestController
@RequestMapping("/trade/announcement") // 请求路径前缀
public class AnnouncementController extends BaseController
{
    @Autowired
    private IAnnouncementService announcementService; // 公告服务

    /**
     * 查询系统公告列表
     */
    @PreAuthorize("@ss.hasPermi('trade:announcement:list')") // 权限校验
    @GetMapping("/list") // GET /trade/announcement/list
    public TableDataInfo list(Announcement announcement)
    {
        startPage(); // 启动PageHelper分页
        List<Announcement> list = announcementService.selectAnnouncementList(announcement); // 查询公告列表
        return getDataTable(list); // 封装分页结果
    }

    /**
     * 用户端查询最新公告
     */
    @Anonymous // 无需登录访问
    @GetMapping("/publicList") // GET /trade/announcement/publicList
    public AjaxResult publicList()
    {
        return success(announcementService.selectPublicAnnouncements()); // 返回公开公告列表
    }

    /**
     * 导出系统公告列表
     */
    @PreAuthorize("@ss.hasPermi('trade:announcement:export')") // 权限校验
    @Log(title = "系统公告", businessType = BusinessType.EXPORT) // 操作日志
    @PostMapping("/export") // POST /trade/announcement/export
    public void export(HttpServletResponse response, Announcement announcement)
    {
        List<Announcement> list = announcementService.selectAnnouncementList(announcement); // 查询全量数据
        ExcelUtil<Announcement> util = new ExcelUtil<Announcement>(Announcement.class); // 创建Excel工具
        util.exportExcel(response, list, "系统公告数据"); // 导出Excel
    }

    /**
     * 获取系统公告详细信息
     */
    @PreAuthorize("@ss.hasPermi('trade:announcement:query')") // 权限校验
    @GetMapping(value = "/{id}") // GET /trade/announcement/{id}
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(announcementService.selectAnnouncementById(id)); // 返回公告详情
    }

    /**
     * 新增系统公告
     */
    @PreAuthorize("@ss.hasPermi('trade:announcement:add')") // 权限校验
    @Log(title = "系统公告", businessType = BusinessType.INSERT) // 操作日志
    @PostMapping // POST /trade/announcement
    public AjaxResult add(@RequestBody Announcement announcement)
    {
        return toAjax(announcementService.insertAnnouncement(announcement)); // 新增并返回结果
    }

    /**
     * 修改系统公告
     */
    @PreAuthorize("@ss.hasPermi('trade:announcement:edit')") // 权限校验
    @Log(title = "系统公告", businessType = BusinessType.UPDATE) // 操作日志
    @PutMapping // PUT /trade/announcement
    public AjaxResult edit(@RequestBody Announcement announcement)
    {
        return toAjax(announcementService.updateAnnouncement(announcement)); // 修改并返回结果
    }

    /**
     * 删除系统公告
     */
    @PreAuthorize("@ss.hasPermi('trade:announcement:remove')") // 权限校验
    @Log(title = "系统公告", businessType = BusinessType.DELETE) // 操作日志
	@DeleteMapping("/{ids}") // DELETE /trade/announcement/{ids}
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(announcementService.deleteAnnouncementByIds(ids)); // 批量删除并返回结果
    }
}
