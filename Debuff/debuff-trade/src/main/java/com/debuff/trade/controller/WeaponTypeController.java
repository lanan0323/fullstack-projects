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
import com.debuff.trade.domain.entity.WeaponType;
import com.debuff.trade.service.IWeaponTypeService;

/**
 * 武器类型 后台管理接口
 * 
 * @author B同学
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/admin/weaponType") // 设置武器类型管理请求路径前缀
public class WeaponTypeController extends BaseController // 继承基础控制器
{
    @Autowired // 自动注入武器类型服务
    private IWeaponTypeService weaponTypeService;

    /**
     * 武器类型列表
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @GetMapping("/list") // GET请求映射到武器类型列表接口
    public TableDataInfo list(WeaponType weaponType) // 分页查询武器类型列表
    {
        startPage(); // 启动PageHelper分页
        List<WeaponType> list = weaponTypeService.selectWeaponTypeList(weaponType); // 调用服务层查询武器类型列表
        return getDataTable(list); // 封装为分页表格数据返回
    }

    /**
     * 武器类型详情
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @GetMapping("/{id}") // GET请求，路径参数为武器类型ID
    public AjaxResult getInfo(@PathVariable Long id) // 根据ID获取武器类型详情
    {
        return success(weaponTypeService.selectWeaponTypeById(id)); // 返回武器类型详细信息
    }

    /**
     * 新增武器类型
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @Log(title = "武器类型管理", businessType = BusinessType.INSERT) // 记录操作日志，类型为新增
    @PostMapping // POST请求处理新增
    public AjaxResult add(@RequestBody WeaponType weaponType) // 接收JSON格式的新增武器类型数据
    {
        return toAjax(weaponTypeService.insertWeaponType(weaponType)); // 调用服务层新增武器类型并返回结果
    }

    /**
     * 修改武器类型
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @Log(title = "武器类型管理", businessType = BusinessType.UPDATE) // 记录操作日志，类型为更新
    @PutMapping // PUT请求处理修改
    public AjaxResult edit(@RequestBody WeaponType weaponType) // 接收JSON格式的修改武器类型数据
    {
        return toAjax(weaponTypeService.updateWeaponType(weaponType)); // 调用服务层更新武器类型并返回结果
    }

    /**
     * 删除武器类型
     */
    @PreAuthorize("@ss.hasRole('admin')") // 权限校验：需要管理员角色
    @Log(title = "武器类型管理", businessType = BusinessType.DELETE) // 记录操作日志，类型为删除
    @DeleteMapping("/{ids}") // DELETE请求，支持批量删除
    public AjaxResult remove(@PathVariable Long[] ids) // 接收多个武器类型ID进行批量删除
    {
        return toAjax(weaponTypeService.deleteWeaponTypeByIds(ids)); // 调用服务层批量删除武器类型并返回结果
    }
}
