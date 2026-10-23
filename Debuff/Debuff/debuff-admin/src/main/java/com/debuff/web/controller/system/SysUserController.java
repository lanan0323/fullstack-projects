// 包声明
package com.debuff.web.controller.system;

// 导入集合、Stream、Servlet、数组工具、权限、验证、Excel和文件上传等依赖
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.entity.SysDept;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.system.service.ISysDeptService;
import com.debuff.system.service.ISysPostService;
import com.debuff.system.service.ISysRoleService;
import com.debuff.system.service.ISysUserService;

/**
 * 用户信息
 * 
 * @author ruoyi
 */
// 标记为REST控制器，映射路径为/system/user
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    // 注入用户服务
    @Autowired
    private ISysUserService userService;

    // 注入角色服务
    @Autowired
    private ISysRoleService roleService;

    // 注入部门服务
    @Autowired
    private ISysDeptService deptService;

    // 注入岗位服务
    @Autowired
    private ISysPostService postService;

    /**
     * 获取用户列表
     */
    // 需要system:user:list权限
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        // 开启分页
        startPage();
        // 查询用户列表
        List<SysUser> list = userService.selectUserList(user);
        // 返回分页数据
        return getDataTable(list);
    }

    // 记录导出日志，需要system:user:export权限
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        // 查询用户列表
        List<SysUser> list = userService.selectUserList(user);
        // 使用Excel工具导出用户数据
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    // 记录导入日志，需要system:user:import权限
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        // 创建Excel工具类
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        // 解析Excel文件中的用户数据
        List<SysUser> userList = util.importExcel(file.getInputStream());
        // 获取当前操作用户名
        String operName = getUsername();
        // 执行用户导入
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    // 下载用户导入模板
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        // 创建Excel工具类并生成模板
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    // 需要system:user:query权限，支持不传userId（新增场景）
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        // 校验数据权限范围
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        // 查询所有角色
        List<SysRole> roles = roleService.selectRoleAll();
        // 管理员返回所有角色，非管理员过滤掉管理员角色
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        // 查询所有岗位
        ajax.put("posts", postService.selectPostAll());
        // 如果传了userId，查询用户详情
        if (StringUtils.isNotNull(userId))
        {
            // 查询用户信息
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            // 查询用户关联的岗位ID列表
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            // 查询用户关联的角色ID列表
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    // 需要system:user:add权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        // 校验用户名是否唯一
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        // 校验手机号是否唯一
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        // 校验邮箱是否唯一
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        // 设置创建人
        user.setCreateBy(getUsername());
        // 密码加密
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        // 执行新增操作
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    // 需要system:user:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        // 检查用户是否允许操作（超级管理员不可修改）
        userService.checkUserAllowed(user);
        // 校验数据权限范围
        userService.checkUserDataScope(user.getUserId());
        // 校验用户名是否唯一
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        // 校验手机号是否唯一
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        // 校验邮箱是否唯一
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        // 设置修改人
        user.setUpdateBy(getUsername());
        // 执行修改操作
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    // 需要system:user:remove权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        // 不允许删除当前登录用户
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        // 批量删除用户
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    // 需要system:user:resetPwd权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        // 检查用户是否允许操作
        userService.checkUserAllowed(user);
        // 校验数据权限范围
        userService.checkUserDataScope(user.getUserId());
        // 密码加密
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        // 设置修改人
        user.setUpdateBy(getUsername());
        // 执行密码重置
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    // 需要system:user:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        // 检查用户是否允许操作
        userService.checkUserAllowed(user);
        // 校验数据权限范围
        userService.checkUserDataScope(user.getUserId());
        // 设置修改人
        user.setUpdateBy(getUsername());
        // 修改用户状态（启用/停用）
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    // 需要system:user:query权限
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        // 查询用户信息
        SysUser user = userService.selectUserById(userId);
        // 查询用户已授权的角色列表
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        // 管理员返回所有角色，非管理员过滤掉管理员角色
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    // 需要system:user:edit权限，记录授权日志
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        // 校验数据权限范围
        userService.checkUserDataScope(userId);
        // 给用户分配角色
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     */
    // 需要system:user:list权限
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        // 查询部门树结构用于用户筛选
        return success(deptService.selectDeptTreeList(dept));
    }
}
