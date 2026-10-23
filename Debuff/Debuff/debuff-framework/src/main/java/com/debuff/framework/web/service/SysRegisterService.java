// 包声明
package com.debuff.framework.web.service;

// 导入所需类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.constant.Constants;
import com.debuff.common.constant.UserConstants;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.RegisterBody;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.exception.user.CaptchaException;
import com.debuff.common.exception.user.CaptchaExpireException;
import com.debuff.common.utils.MessageUtils;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.framework.manager.AsyncManager;
import com.debuff.framework.manager.factory.AsyncFactory;
import com.debuff.system.service.ISysConfigService;
import com.debuff.system.service.ISysUserService;

/**
 * 注册校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService; // 用户服务，用于注册用户

    @Autowired
    private ISysConfigService configService; // 系统配置服务，获取验证码开关

    @Autowired
    private RedisCache redisCache; // Redis缓存，用于校验验证码

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword(); // 获取注册参数
        SysUser sysUser = new SysUser(); // 创建用户对象
        sysUser.setUserName(username); // 设置用户名

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled(); // 查询验证码是否开启
        if (captchaEnabled) // 验证码开启时需要校验
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid()); // 校验验证码
        }

        if (StringUtils.isEmpty(username)) // 用户名为空
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password)) // 密码为空
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) // 用户名长度不合法
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) // 密码长度不合法
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(sysUser)) // 用户名已存在
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else // 校验通过，执行注册
        {
            sysUser.setNickName(username); // 设置昵称等于用户名
            sysUser.setPassword(SecurityUtils.encryptPassword(password)); // BCrypt加密密码
            boolean regFlag = userService.registerUser(sysUser); // 插入用户到数据库
            if (!regFlag) // 注册失败
            {
                msg = "注册失败,请联系系统管理人员";
            }
            else // 注册成功
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success"))); // 异步记录注册成功日志
            }
        }
        return msg; // 返回消息(空字符串表示成功)
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, ""); // 拼接Redis验证码key
        String captcha = redisCache.getCacheObject(verifyKey); // 从Redis获取验证码
        redisCache.deleteObject(verifyKey); // 验证码用后即删
        if (captcha == null) // 验证码已过期
        {
            throw new CaptchaExpireException(); // 抛出验证码过期异常
        }
        if (!code.equalsIgnoreCase(captcha)) // 验证码不匹配
        {
            throw new CaptchaException(); // 抛出验证码错误异常
        }
    }
}
