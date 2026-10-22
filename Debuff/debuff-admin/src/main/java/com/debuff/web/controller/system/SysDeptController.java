// 包声明
package com.debuff.web.controller.system;

// 导入集合、数组工具、权限、验证、Spring MVC和日志等依赖
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
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
import com.debuff.common.constant.UserConstants;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.entity.SysDept;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.utils.StringUtils;
import com.debuff.system.service.ISysDeptService;

/**
 * 部门信息
 * 
 * @author ruoyi
 */
// 标记为REST控制器，映射路径为/system/dept
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController
{
    // 注入部门服务
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    // 需要system:dept:list权限
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public AjaxResult list(SysDept dept)
    {
        // 查询部门列表
        List<SysDept> depts = deptService.selectDeptList(dept);
        return success(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    // 需要system:dept:list权限，用于编辑时排除自身及其子节点
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        // 查询所有部门列表
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        // 移除指定部门及其所有子部门（通过祖先链判断）
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    // 需要system:dept:query权限
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId)
    {
        // 校验数据权限范围
        deptService.checkDeptDataScope(deptId);
        // 根据ID查询部门详情
        return success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    // 需要system:dept:add权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDept dept)
    {
        // 校验部门名称是否唯一
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        // 设置创建人
        dept.setCreateBy(getUsername());
        // 执行新增操作
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    // 需要system:dept:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDept dept)
    {
        // 获取部门ID
        Long deptId = dept.getDeptId();
        // 校验数据权限范围
        deptService.checkDeptDataScope(deptId);
        // 校验部门名称是否唯一
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        // 上级部门不能是自己
        else if (dept.getParentId().equals(deptId))
        {
            return error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        // 如果停用该部门，但存在未停用的子部门则不允许
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0)
        {
            return error("该部门包含未停用的子部门！");
        }
        // 设置修改人
        dept.setUpdateBy(getUsername());
        // 执行修改操作
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    // 需要system:dept:remove权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId)
    {
        // 检查是否存在下级部门
        if (deptService.hasChildByDeptId(deptId))
        {
            return warn("存在下级部门,不允许删除");
        }
        // 检查部门下是否有用户
        if (deptService.checkDeptExistUser(deptId))
        {
            return warn("部门存在用户,不允许删除");
        }
        // 校验数据权限范围
        deptService.checkDeptDataScope(deptId);
        // 执行删除操作
        return toAjax(deptService.deleteDeptById(deptId));
    }
}
