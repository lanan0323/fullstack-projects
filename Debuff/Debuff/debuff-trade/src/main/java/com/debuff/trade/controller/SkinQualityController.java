// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.util.List;
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
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.trade.domain.entity.SkinQuality;
import com.debuff.trade.service.ISkinQualityService;

/**
 * 饰品品质 后台管理接口
 * 
 * @author B同学
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/admin/quality") // 设置品质管理请求路径前缀
public class SkinQualityController extends BaseController // 继承基础控制器
{
    @Autowired // 自动注入品质服务
    private ISkinQualityService skinQualityService;

    /**
     * 品质列表
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @GetMapping("/list") // GET请求映射到品质列表接口
    public TableDataInfo list(SkinQuality skinQuality) // 分页查询品质列表
    {
        startPage(); // 启动PageHelper分页
        List<SkinQuality> list = skinQualityService.selectSkinQualityList(skinQuality); // 调用服务层查询品质列表
        return getDataTable(list); // 封装为分页表格数据返回
    }

    /**
     * 品质详情
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @GetMapping("/{id}") // GET请求，路径参数为品质ID
    public AjaxResult getInfo(@PathVariable Long id) // 根据ID获取品质详情
    {
        return success(skinQualityService.selectSkinQualityById(id)); // 返回品质详细信息
    }

    /**
     * 新增品质
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @Log(title = "饰品品质管理", businessType = BusinessType.INSERT) // 记录操作日志，类型为新增
    @PostMapping // POST请求处理新增
    public AjaxResult add(@RequestBody SkinQuality skinQuality) // 接收JSON格式的新增品质数据
    {
        return toAjax(skinQualityService.insertSkinQuality(skinQuality)); // 调用服务层新增品质并返回结果
    }

    /**
     * 修改品质
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @Log(title = "饰品品质管理", businessType = BusinessType.UPDATE) // 记录操作日志，类型为更新
    @PutMapping // PUT请求处理修改
    public AjaxResult edit(@RequestBody SkinQuality skinQuality) // 接收JSON格式的修改品质数据
    {
        return toAjax(skinQualityService.updateSkinQuality(skinQuality)); // 调用服务层更新品质并返回结果
    }

    /**
     * 删除品质
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @Log(title = "饰品品质管理", businessType = BusinessType.DELETE) // 记录操作日志，类型为删除
    @DeleteMapping("/{ids}") // DELETE请求，支持批量删除
    public AjaxResult remove(@PathVariable Long[] ids) // 接收多个品质ID进行批量删除
    {
        return toAjax(skinQualityService.deleteSkinQualityByIds(ids)); // 调用服务层批量删除品质并返回结果
    }
}
