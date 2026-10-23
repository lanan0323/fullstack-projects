// 包声明
package com.debuff.common.exception.user;

// 导入基础异常类
import com.debuff.common.exception.base.BaseException;

/**
 * 用户信息异常类
 * 
 * @author ruoyi
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    public UserException(String code, Object[] args) // 用户异常构造方法,模块固定为user
    {
        super("user", code, args, null);
    }
}
