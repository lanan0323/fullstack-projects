// 包声明
package com.debuff.framework.web.service;

// 导入所需类
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.constant.Constants;
import com.debuff.common.constant.UserConstants;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.exception.user.BlackListException;
import com.debuff.common.exception.user.CaptchaException;
import com.debuff.common.exception.user.CaptchaExpireException;
import com.debuff.common.exception.user.UserNotExistsException;
import com.debuff.common.exception.user.UserPasswordNotMatchException;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.MessageUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.ip.IpUtils;
import com.debuff.framework.manager.AsyncManager;
import com.debuff.framework.manager.factory.AsyncFactory;
import com.debuff.framework.security.context.AuthenticationContextHolder;
import com.debuff.system.service.ISysConfigService;
import com.debuff.system.service.ISysUserService;

/**
 * 登录校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysLoginService
{
    @Autowired
    private TokenService tokenService; // Token服务，用于生成JWT令牌

    @Resource
    private AuthenticationManager authenticationManager; // Spring Security认证管理器

    @Autowired
    private RedisCache redisCache; // Redis缓存，用于读取验证码
    
    @Autowired
    private ISysUserService userService; // 用户服务，用于更新登录信息

    @Autowired
    private ISysConfigService configService; // 系统配置服务，获取验证码开关等

    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid)
    {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验(用户名/密码格式、IP黑名单)
        loginPreCheck(username, password);
        // 用户验证
        Authentication authentication = null; // 认证结果对象
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password); // 构建认证令牌
            AuthenticationContextHolder.setContext(authenticationToken); // 将认证令牌存入ThreadLocal上下文
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken); // 执行认证，内部调用loadUserByUsername
        }
        catch (Exception e) // 认证失败异常处理
        {
            if (e instanceof BadCredentialsException) // 密码错误异常
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"))); // 异步记录登录失败日志
                throw new UserPasswordNotMatchException(); // 抛出用户名或密码错误异常
            }
            else // 其他认证异常
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage())); // 异步记录登录失败日志
                throw new ServiceException(e.getMessage()); // 抛出通用服务异常
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext(); // 清除ThreadLocal中的认证上下文，防止内存泄漏
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"))); // 异步记录登录成功日志
        LoginUser loginUser = (LoginUser) authentication.getPrincipal(); // 获取认证通过后的登录用户对象
        recordLoginInfo(loginUser.getUserId()); // 记录用户的登录IP和时间
        // 生成token
        return tokenService.createToken(loginUser); // 创建并返回JWT令牌字符串
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
        boolean captchaEnabled = configService.selectCaptchaEnabled(); // 查询验证码是否开启
        if (captchaEnabled) // 验证码开启时校验
        {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, ""); // 拼接Redis中验证码的key
            String captcha = redisCache.getCacheObject(verifyKey); // 从Redis获取验证码
            redisCache.deleteObject(verifyKey); // 验证码用后即删，一次性有效
            if (captcha == null) // 验证码为空说明已过期
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"))); // 记录验证码过期日志
                throw new CaptchaExpireException(); // 抛出验证码过期异常
            }
            if (!code.equalsIgnoreCase(captcha)) // 验证码不匹配
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"))); // 记录验证码错误日志
                throw new CaptchaException(); // 抛出验证码错误异常
            }
        }
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) // 用户名或密码为空
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null"))); // 记录登录失败日志
            throw new UserNotExistsException(); // 抛出用户不存在异常
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) // 密码长度不在合法范围内
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"))); // 记录登录失败日志
            throw new UserPasswordNotMatchException(); // 抛出密码不匹配异常
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) // 用户名长度不在合法范围内
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match"))); // 记录登录失败日志
            throw new UserPasswordNotMatchException(); // 抛出密码不匹配异常
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList"); // 获取IP黑名单配置
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) // 当前IP在黑名单中
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked"))); // 记录登录被阻止日志
            throw new BlackListException(); // 抛出黑名单异常
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser(); // 创建用户对象
        sysUser.setUserId(userId); // 设置用户ID
        sysUser.setLoginIp(IpUtils.getIpAddr()); // 设置登录IP
        sysUser.setLoginDate(DateUtils.getNowDate()); // 设置登录时间
        userService.updateUserProfile(sysUser); // 更新用户登录信息到数据库
    }
}
