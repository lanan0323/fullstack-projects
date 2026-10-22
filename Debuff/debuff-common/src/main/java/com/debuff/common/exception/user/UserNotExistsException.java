// 包声明
package com.debuff.common.exception.user;

/**
 * 用户不存在异常类
 * 
 * @author ruoyi
 */
public class UserNotExistsException extends UserException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public UserNotExistsException() // 用户不存在,使用国际化key
    {
        super("user.not.exists", null);
    }
}
