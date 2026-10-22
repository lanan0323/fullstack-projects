// 包声明
package com.debuff.web.controller.system;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.system.domain.SysNotice;
import com.debuff.system.service.ISysNoticeService;

/**
 * 公告 信息操作处理
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/system/notice") // 请求路径前缀
public class SysNoticeController extends BaseController
{
    @Autowired // 自动注入公告服务
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')") // 权限校验：公告列表
    @GetMapping("/list") // GET请求
    public TableDataInfo list(SysNotice notice)
    {
        startPage(); // 设置分页参数
        List<SysNotice> list = noticeService.selectNoticeList(notice); // 查询公告列表
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:notice:query')") // 权限校验：公告查询
    @GetMapping(value = "/{noticeId}") // GET请求，路径带ID
    public AjaxResult getInfo(@PathVariable Long noticeId)
    {
        return success(noticeService.selectNoticeById(noticeId)); // 根据ID查询公告详情
    }

    /**
     * 新增通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:add')") // 权限校验：公告新增
    @Log(title = "通知公告", businessType = BusinessType.INSERT) // 记录操作日志
    @PostMapping // POST请求
    public AjaxResult add(@Validated @RequestBody SysNotice notice)
    {
        notice.setCreateBy(getUsername()); // 设置创建者为当前登录用户
        return toAjax(noticeService.insertNotice(notice)); // 插入公告并返回结果
    }

    /**
     * 修改通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:edit')") // 权限校验：公告编辑
    @Log(title = "通知公告", businessType = BusinessType.UPDATE) // 记录操作日志
    @PutMapping // PUT请求
    public AjaxResult edit(@Validated @RequestBody SysNotice notice)
    {
        notice.setUpdateBy(getUsername()); // 设置修改者为当前登录用户
        return toAjax(noticeService.updateNotice(notice)); // 更新公告并返回结果
    }

    /**
     * 删除通知公告
     */
    @PreAuthorize("@ss.hasPermi('system:notice:remove')") // 权限校验：公告删除
    @Log(title = "通知公告", businessType = BusinessType.DELETE) // 记录操作日志
    @DeleteMapping("/{noticeIds}") // DELETE请求，支持批量
    public AjaxResult remove(@PathVariable Long[] noticeIds)
    {
        return toAjax(noticeService.deleteNoticeByIds(noticeIds)); // 批量删除公告
    }
}
