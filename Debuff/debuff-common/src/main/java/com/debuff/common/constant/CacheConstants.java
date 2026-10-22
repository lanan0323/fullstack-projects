// 包声明：缓存常量所在包
package com.debuff.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author ruoyi
 */
public class CacheConstants
{
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:"; // Redis中存储登录令牌的key前缀

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:"; // Redis中存储验证码的key前缀

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:"; // Redis中存储系统参数的key前缀

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:"; // Redis中存储字典数据的key前缀

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:"; // Redis中防重复提交的key前缀

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:"; // Redis中限流计数的key前缀

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:"; // Redis中记录密码错误次数的key前缀
}
