// 包声明
package com.debuff.framework.web.service;

// 导入所需类
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.framework.security.context.PermissionContextHolder;

/**
 * RuoYi首创 自定义权限实现，ss取自SpringSecurity首字母
 * 
 * @author ruoyi
 */
@Service("ss") // 注册为Spring Bean，在模板/注解中用@ss.hasPermi('xxx')调用
public class PermissionService
{
    /**
     * 验证用户是否具备某权限
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public boolean hasPermi(String permission)
    {
        if (StringUtils.isEmpty(permission)) // 权限字符串为空
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser(); // 获取当前登录用户
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions())) // 未登录或无权限
        {
            return false;
        }
        PermissionContextHolder.setContext(permission); // 将权限标识存入请求上下文(供数据权限过滤使用)
        return hasPermissions(loginUser.getPermissions(), permission); // 判断用户权限集合是否包含该权限
    }

    /**
     * 验证用户是否不具备某权限，与 hasPermi逻辑相反
     *
     * @param permission 权限字符串
     * @return 用户是否不具备某权限
     */
    public boolean lacksPermi(String permission)
    {
        return hasPermi(permission) != true; // 取反
    }

    /**
     * 验证用户是否具有以下任意一个权限
     *
     * @param permissions 以 PERMISSION_DELIMETER 为分隔符的权限列表
     * @return 用户是否具有以下任意一个权限
     */
    public boolean hasAnyPermi(String permissions)
    {
        if (StringUtils.isEmpty(permissions)) // 权限字符串为空
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser(); // 获取当前登录用户
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions())) // 未登录或无权限
        {
            return false;
        }
        PermissionContextHolder.setContext(permissions); // 将权限标识存入请求上下文
        Set<String> authorities = loginUser.getPermissions(); // 获取用户权限集合
        for (String permission : permissions.split(Constants.PERMISSION_DELIMETER)) // 按分隔符拆分权限
        {
            if (permission != null && hasPermissions(authorities, permission)) // 匹配任意一个权限
            {
                return true; // 有任意一个权限即返回true
            }
        }
        return false; // 都不匹配返回false
    }

    /**
     * 判断用户是否拥有某个角色
     * 
     * @param role 角色字符串
     * @return 用户是否具备某角色
     */
    public boolean hasRole(String role)
    {
        if (StringUtils.isEmpty(role)) // 角色字符串为空
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser(); // 获取当前登录用户
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles())) // 未登录或无角色
        {
            return false;
        }
        for (SysRole sysRole : loginUser.getUser().getRoles()) // 遍历用户角色
        {
            String roleKey = sysRole.getRoleKey(); // 获取角色标识
            if (Constants.SUPER_ADMIN.equals(roleKey) || roleKey.equals(StringUtils.trim(role))) // 超管角色直接通过，或角色标识匹配
            {
                return true;
            }
        }
        return false; // 不匹配返回false
    }

    /**
     * 验证用户是否不具备某角色，与 isRole逻辑相反。
     *
     * @param role 角色名称
     * @return 用户是否不具备某角色
     */
    public boolean lacksRole(String role)
    {
        return hasRole(role) != true; // 取反
    }

    /**
     * 验证用户是否具有以下任意一个角色
     *
     * @param roles 以 ROLE_NAMES_DELIMETER 为分隔符的角色列表
     * @return 用户是否具有以下任意一个角色
     */
    public boolean hasAnyRoles(String roles)
    {
        if (StringUtils.isEmpty(roles)) // 角色字符串为空
        {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser(); // 获取当前登录用户
        if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getUser().getRoles())) // 未登录或无角色
        {
            return false;
        }
        for (String role : roles.split(Constants.ROLE_DELIMETER)) // 按分隔符拆分角色
        {
            if (hasRole(role)) // 匹配任意一个角色
            {
                return true; // 有任意一个角色即返回true
            }
        }
        return false; // 都不匹配返回false
    }

    /**
     * 判断是否包含权限
     * 
     * @param permissions 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission)
    {
        return permissions.contains(Constants.ALL_PERMISSION) || permissions.contains(StringUtils.trim(permission)); // 全权限标识或精确匹配
    }
}
