// 包声明
package com.debuff.common.exception.user;

/**
 * 黑名单IP异常类
 * 
 * @author ruoyi
 */
public class BlackListException extends UserException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public BlackListException() // IP在黑名单中,使用国际化key
    {
        super("login.blocked", null);
    }
}
