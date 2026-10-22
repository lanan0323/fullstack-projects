// 包声明
package com.debuff.system.service.impl;

// 导入相关依赖
import org.springframework.stereotype.Service;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.utils.StringUtils;
import com.debuff.system.domain.SysUserOnline;
import com.debuff.system.service.ISysUserOnlineService;

/**
 * 在线用户 服务层处理
 * 
 * @author ruoyi
 */
@Service // 注册为Spring服务Bean
public class SysUserOnlineServiceImpl implements ISysUserOnlineService
{
    /**
     * 通过登录地址查询信息
     * 
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr())) // 匹配登录IP地址
        {
            return loginUserToUserOnline(user); // 转换为在线用户对象
        }
        return null; // IP不匹配返回null
    }

    /**
     * 通过用户名称查询信息
     * 
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user)
    {
        if (StringUtils.equals(userName, user.getUsername())) // 匹配用户名
        {
            return loginUserToUserOnline(user); // 转换为在线用户对象
        }
        return null; // 用户名不匹配返回null
    }

    /**
     * 通过登录地址/用户名称查询信息
     * 
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername())) // 同时匹配IP和用户名
        {
            return loginUserToUserOnline(user); // 转换为在线用户对象
        }
        return null; // 不匹配返回null
    }

    /**
     * 设置在线用户信息
     * 
     * @param user 用户信息
     * @return 在线用户
     */
    @Override
    public SysUserOnline loginUserToUserOnline(LoginUser user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUser())) // 用户信息为空则返回null
        {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline(); // 创建在线用户对象
        sysUserOnline.setTokenId(user.getToken()); // 设置token标识
        sysUserOnline.setUserName(user.getUsername()); // 设置用户名
        sysUserOnline.setIpaddr(user.getIpaddr()); // 设置登录IP
        sysUserOnline.setLoginLocation(user.getLoginLocation()); // 设置登录地点
        sysUserOnline.setBrowser(user.getBrowser()); // 设置浏览器类型
        sysUserOnline.setOs(user.getOs()); // 设置操作系统
        sysUserOnline.setLoginTime(user.getLoginTime()); // 设置登录时间
        if (StringUtils.isNotNull(user.getUser().getDept())) // 用户有所属部门信息
        {
            sysUserOnline.setDeptName(user.getUser().getDept().getDeptName()); // 设置部门名称
        }
        return sysUserOnline; // 返回在线用户对象
    }
}
