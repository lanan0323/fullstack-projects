// 包声明
package com.debuff.web.controller.system;

// 导入集合、Servlet、权限、验证、Excel导出、Token和日志等依赖
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.debuff.common.core.domain.entity.SysDept;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.framework.web.service.SysPermissionService;
import com.debuff.framework.web.service.TokenService;
import com.debuff.system.domain.SysUserRole;
import com.debuff.system.service.ISysDeptService;
import com.debuff.system.service.ISysRoleService;
import com.debuff.system.service.ISysUserService;

/**
 * 角色信息
 * 
 * @author ruoyi
 */
// 标记为REST控制器，映射路径为/system/role
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController
{
    // 注入角色服务
    @Autowired
    private ISysRoleService roleService;

    // 注入Token服务，用于更新缓存中的用户信息
    @Autowired
    private TokenService tokenService;

    // 注入权限服务
    @Autowired
    private SysPermissionService permissionService;

    // 注入用户服务
    @Autowired
    private ISysUserService userService;

    // 注入部门服务
    @Autowired
    private ISysDeptService deptService;

    // 需要system:role:list权限
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRole role)
    {
        // 开启分页
        startPage();
        // 查询角色列表
        List<SysRole> list = roleService.selectRoleList(role);
        // 返回分页数据
        return getDataTable(list);
    }

    // 记录导出日志，需要system:role:export权限
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role)
    {
        // 查询角色列表
        List<SysRole> list = roleService.selectRoleList(role);
        // 使用Excel工具导出角色数据
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    // 需要system:role:query权限
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId)
    {
        // 校验数据权限范围
        roleService.checkRoleDataScope(roleId);
        // 根据ID查询角色详情
        return success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    // 需要system:role:add权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysRole role)
    {
        // 校验角色名称是否唯一
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        // 校验角色权限标识是否唯一
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        // 设置创建人
        role.setCreateBy(getUsername());
        // 执行新增操作
        return toAjax(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    // 需要system:role:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysRole role)
    {
        // 检查角色是否允许操作（超级管理员角色不可修改）
        roleService.checkRoleAllowed(role);
        // 校验数据权限范围
        roleService.checkRoleDataScope(role.getRoleId());
        // 校验角色名称是否唯一
        if (!roleService.checkRoleNameUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        // 校验角色权限标识是否唯一
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return error("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        // 设置修改人
        role.setUpdateBy(getUsername());
        
        // 如果修改成功
        if (roleService.updateRole(role) > 0)
        {
            // 更新缓存用户权限
            // 获取当前登录用户
            LoginUser loginUser = getLoginUser();
            // 如果不是管理员用户，刷新其权限缓存
            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isAdmin())
            {
                // 重新获取菜单权限
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                // 重新加载用户信息
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                // 更新Token中的用户信息
                tokenService.setLoginUser(loginUser);
            }
            return success();
        }
        return error("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     */
    // 需要system:role:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public AjaxResult dataScope(@RequestBody SysRole role)
    {
        // 检查角色是否允许操作
        roleService.checkRoleAllowed(role);
        // 校验数据权限范围
        roleService.checkRoleDataScope(role.getRoleId());
        // 授权数据权限范围
        return toAjax(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    // 需要system:role:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysRole role)
    {
        // 检查角色是否允许操作
        roleService.checkRoleAllowed(role);
        // 校验数据权限范围
        roleService.checkRoleDataScope(role.getRoleId());
        // 设置修改人
        role.setUpdateBy(getUsername());
        // 修改角色状态
        return toAjax(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    // 需要system:role:remove权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds)
    {
        // 批量删除角色
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    // 需要system:role:query权限
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        // 查询所有角色
        return success(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    // 需要system:role:list权限
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo allocatedList(SysUser user)
    {
        // 开启分页
        startPage();
        // 查询已分配该角色的用户列表
        List<SysUser> list = userService.selectAllocatedList(user);
        return getDataTable(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    // 需要system:role:list权限
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo unallocatedList(SysUser user)
    {
        // 开启分页
        startPage();
        // 查询未分配该角色的用户列表
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getDataTable(list);
    }

    /**
     * 取消授权用户
     */
    // 需要system:role:edit权限，记录授权日志
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public AjaxResult cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        // 取消指定用户的角色授权
        return toAjax(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    // 需要system:role:edit权限，记录授权日志
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public AjaxResult cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        // 批量取消用户角色授权
        return toAjax(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    // 需要system:role:edit权限，记录授权日志
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public AjaxResult selectAuthUserAll(Long roleId, Long[] userIds)
    {
        // 校验数据权限范围
        roleService.checkRoleDataScope(roleId);
        // 批量给用户授权角色
        return toAjax(roleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * 获取对应角色部门树列表
     */
    // 需要system:role:query权限
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/deptTree/{roleId}")
    public AjaxResult deptTree(@PathVariable("roleId") Long roleId)
    {
        AjaxResult ajax = AjaxResult.success();
        // 返回该角色已勾选的部门ID集合
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        // 返回部门树列表
        ajax.put("depts", deptService.selectDeptTreeList(new SysDept()));
        return ajax;
    }
}
