// 包声明
package com.debuff.framework.web.service;

// 导入所需类
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.system.service.ISysMenuService;
import com.debuff.system.service.ISysRoleService;

/**
 * 用户权限处理
 * 
 * @author ruoyi
 */
@Component
public class SysPermissionService
{
    @Autowired
    private ISysRoleService roleService; // 角色服务，用于查询用户角色

    @Autowired
    private ISysMenuService menuService; // 菜单服务，用于查询菜单权限

    /**
     * 获取角色数据权限
     * 
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>(); // 角色权限集合
        // 管理员拥有所有权限
        if (user.isAdmin()) // 超级管理员直接返回admin角色
        {
            roles.add("admin");
        }
        else // 普通用户查询其角色列表
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId())); // 根据用户ID查询角色标识集合
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     * 
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> perms = new HashSet<String>(); // 菜单权限集合
        // 管理员拥有所有权限
        if (user.isAdmin()) // 超级管理员直接返回所有权限标识
        {
            perms.add("*:*:*");
        }
        else // 普通用户查询其菜单权限
        {
            List<SysRole> roles = user.getRoles(); // 获取用户角色列表
            if (!CollectionUtils.isEmpty(roles)) // 角色列表不为空
            {
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (SysRole role : roles) // 遍历每个角色
                {
                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId()); // 根据角色ID查询菜单权限
                    role.setPermissions(rolePerms); // 将权限设置到角色对象(供数据权限过滤使用)
                    perms.addAll(rolePerms); // 合并到总权限集合
                }
            }
            else // 无角色的用户
            {
                perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId())); // 直接根据用户ID查询权限
            }
        }
        return perms;
    }
}
