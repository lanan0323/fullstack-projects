// 包声明
package com.debuff.common.utils;

// 导入Spring Security和密码加密相关类
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.PatternMatchUtils;
import com.debuff.common.constant.Constants;
import com.debuff.common.constant.HttpStatus;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.exception.ServiceException;

/**
 * 安全服务工具类
 * 
 * @author ruoyi
 */
public class SecurityUtils
{

    /**
     * 用户ID
     **/
    public static Long getUserId() // 获取当前登录用户ID
    {
        try
        {
            return getLoginUser().getUserId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId() // 获取当前登录用户部门ID
    {
        try
        {
            return getLoginUser().getDeptId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户账户
     **/
    public static String getUsername() // 获取当前登录用户名
    {
        try
        {
            return getLoginUser().getUsername();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() // 获取当前登录用户对象
    {
        try
        {
            return (LoginUser) getAuthentication().getPrincipal(); // 从SecurityContext获取
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() // 获取Spring Security认证对象
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) // BCrypt加密密码
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) // 验证密码是否匹配
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) // 判断是否为管理员(userId=1)
    {
        return userId != null && 1L == userId;
    }

    /**
     * 验证用户是否具备某权限
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermi(String permission) // 检查当前用户是否有指定权限
    {
        return hasPermi(getLoginUser().getPermissions(), permission);
    }

    /**
     * 判断是否包含权限
     * 
     * @param authorities 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermi(Collection<String> authorities, String permission) // 检查权限列表是否包含指定权限
    {
        return authorities.stream().filter(StringUtils::hasText) // 过滤空权限
                .anyMatch(x -> Constants.ALL_PERMISSION.equals(x) || PatternMatchUtils.simpleMatch(x, permission)); // 全权限或模式匹配
    }

    /**
     * 验证用户是否拥有某个角色
     * 
     * @param role 角色标识
     * @return 用户是否具备某角色
     */
    public static boolean hasRole(String role) // 检查当前用户是否有指定角色
    {
        List<SysRole> roleList = getLoginUser().getUser().getRoles(); // 获取用户角色列表
        Collection<String> roles = roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toSet()); // 提取角色key
        return hasRole(roles, role);
    }

    /**
     * 判断是否包含角色
     * 
     * @param roles 角色列表
     * @param role 角色
     * @return 用户是否具备某角色权限
     */
    public static boolean hasRole(Collection<String> roles, String role) // 检查角色列表是否包含指定角色
    {
        return roles.stream().filter(StringUtils::hasText) // 过滤空角色
                .anyMatch(x -> Constants.SUPER_ADMIN.equals(x) || PatternMatchUtils.simpleMatch(x, role)); // 超级管理员或模式匹配
    }

}
