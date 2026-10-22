// 包声明
package com.debuff.framework.web.service;

// 导入所需类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.enums.UserStatus;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.MessageUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.system.service.ISysUserService;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class); // 日志对象

    @Autowired
    private ISysUserService userService; // 用户服务，查询用户信息
    
    @Autowired
    private SysPasswordService passwordService; // 密码服务，校验密码和错误次数

    @Autowired
    private SysPermissionService permissionService; // 权限服务，获取菜单权限

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysUser user = userService.selectUserByUserName(username); // 根据用户名查询用户
        if (StringUtils.isNull(user)) // 用户不存在
        {
            log.info("登录用户：{} 不存在.", username); // 记录日志
            throw new ServiceException(MessageUtils.message("user.not.exists")); // 抛出用户不存在异常
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) // 用户已被删除
        {
            log.info("登录用户：{} 已被删除.", username); // 记录日志
            throw new ServiceException(MessageUtils.message("user.password.delete")); // 抛出用户已删除异常
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) // 用户已被停用
        {
            log.info("登录用户：{} 已被停用.", username); // 记录日志
            throw new ServiceException(MessageUtils.message("user.blocked")); // 抛出用户已停用异常
        }

        passwordService.validate(user); // 校验密码(含错误次数限制)

        return createLoginUser(user); // 创建并返回登录用户对象
    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user)); // 构建LoginUser，包含用户信息和菜单权限
    }
}
