// 包声明：登录模型对象所在包
package com.debuff.common.core.domain.model;

/**
 * 用户登录对象
 * 
 * @author ruoyi
 */
public class LoginBody // 用户登录请求体
{
    /**
     * 用户名
     */
    private String username; // 登录用户名

    /**
     * 用户密码
     */
    private String password; // 登录密码

    /**
     * 验证码
     */
    private String code; // 图形验证码

    /**
     * 唯一标识
     */
    private String uuid; // 验证码对应的唯一标识

    public String getUsername() { return username; } // 获取用户名

    public void setUsername(String username) { this.username = username; } // 设置用户名

    public String getPassword() { return password; } // 获取密码

    public void setPassword(String password) { this.password = password; } // 设置密码

    public String getCode() { return code; } // 获取验证码

    public void setCode(String code) { this.code = code; } // 设置验证码

    public String getUuid() { return uuid; } // 获取唯一标识

    public void setUuid(String uuid) { this.uuid = uuid; } // 设置唯一标识
}
