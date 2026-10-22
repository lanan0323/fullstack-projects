// 包声明
package com.debuff.common.exception.user;

/**
 * 验证码失效异常类
 * 
 * @author ruoyi
 */
public class CaptchaExpireException extends UserException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public CaptchaExpireException() // 验证码过期,使用国际化key
    {
        super("user.jcaptcha.expire", null);
    }
}
