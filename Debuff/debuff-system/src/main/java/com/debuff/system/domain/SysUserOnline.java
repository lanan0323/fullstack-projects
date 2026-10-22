// 包声明：系统模块的实体类包
package com.debuff.system.domain;

/**
 * 当前在线会话
 * 
 * @author ruoyi
 */
public class SysUserOnline // 在线用户会话实体类，用于展示当前在线用户信息
{
    /** 会话编号 */
    private String tokenId; // Token标识，用于唯一标识一个用户会话

    /** 部门名称 */
    private String deptName;

    /** 用户名称 */
    private String userName;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录地址 */
    private String loginLocation; // 根据IP解析的地理位置

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录时间 */
    private Long loginTime; // 登录时间戳

    public String getTokenId() // 获取会话编号
    {
        return tokenId;
    }

    public void setTokenId(String tokenId) // 设置会话编号
    {
        this.tokenId = tokenId;
    }

    public String getDeptName() // 获取部门名称
    {
        return deptName;
    }

    public void setDeptName(String deptName) // 设置部门名称
    {
        this.deptName = deptName;
    }

    public String getUserName() // 获取用户名称
    {
        return userName;
    }

    public void setUserName(String userName) // 设置用户名称
    {
        this.userName = userName;
    }

    public String getIpaddr() // 获取登录IP地址
    {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) // 设置登录IP地址
    {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() // 获取登录地点
    {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) // 设置登录地点
    {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() // 获取浏览器类型
    {
        return browser;
    }

    public void setBrowser(String browser) // 设置浏览器类型
    {
        this.browser = browser;
    }

    public String getOs() // 获取操作系统
    {
        return os;
    }

    public void setOs(String os) // 设置操作系统
    {
        this.os = os;
    }

    public Long getLoginTime() // 获取登录时间
    {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) // 设置登录时间
    {
        this.loginTime = loginTime;
    }
}
