// 包声明
package com.debuff.system.service;

// 导入登录用户模型和在线用户实体类
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.system.domain.SysUserOnline;

/**
 * 在线用户 服务层
 * 
 * @author ruoyi
 */
// 在线用户服务接口，定义在线用户信息的查询和转换方法
public interface ISysUserOnlineService
{
    /**
     * 通过登录地址查询信息
     * 
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    // 根据IP地址构建在线用户信息对象
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user);

    /**
     * 通过用户名称查询信息
     * 
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    // 根据用户名构建在线用户信息对象
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user);

    /**
     * 通过登录地址/用户名称查询信息
     * 
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    // 根据IP和用户名构建在线用户信息对象
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user);

    /**
     * 设置在线用户信息
     * 
     * @param user 用户信息
     * @return 在线用户
     */
    // 将登录用户信息转换为在线用户实体
    public SysUserOnline loginUserToUserOnline(LoginUser user);
}
