// 包声明
package com.debuff.common.exception.user;

/**
 * 验证码错误异常类
 * 
 * @author ruoyi
 */
public class CaptchaException extends UserException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public CaptchaException() // 验证码错误,使用国际化key
    {
        super("user.jcaptcha.error", null);
    }
}
