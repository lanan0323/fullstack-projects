// 包声明
package com.debuff.common.exception.user;

/**
 * 用户密码不正确或不符合规范异常类
 * 
 * @author ruoyi
 */
public class UserPasswordNotMatchException extends UserException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public UserPasswordNotMatchException() // 密码不匹配,使用国际化key
    {
        super("user.password.not.match", null);
    }
}
