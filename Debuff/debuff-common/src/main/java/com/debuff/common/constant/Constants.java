// 包声明：通用常量所在包
package com.debuff.common.constant;

// 导入JWT Claims类
import io.jsonwebtoken.Claims;

/**
 * 通用常量信息
 * 
 * @author ruoyi
 */
public class Constants
{
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8"; // UTF-8编码格式

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK"; // GBK编码格式

    /**
     * www主域
     */
    public static final String WWW = "www."; // www域名前缀

    /**
     * http请求
     */
    public static final String HTTP = "http://"; // HTTP协议前缀

    /**
     * https请求
     */
    public static final String HTTPS = "https://"; // HTTPS协议前缀

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0"; // 成功标识，值为"0"

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1"; // 失败标识，值为"1"

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success"; // 登录成功标识

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout"; // 注销操作标识

    /**
     * 注册
     */
    public static final String REGISTER = "Register"; // 注册操作标识

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error"; // 登录失败标识

    /**
     * 所有权限标识
     */
    public static final String ALL_PERMISSION = "*:*:*"; // 超级管理员拥有所有权限的标识

    /**
     * 管理员角色权限标识
     */
    public static final String SUPER_ADMIN = "admin"; // 管理员角色标识

    /**
     * 角色权限分隔符
     */
    public static final String ROLE_DELIMETER = ","; // 多个角色之间的分隔符

    /**
     * 权限标识分隔符
     */
    public static final String PERMISSION_DELIMETER = ","; // 多个权限之间的分隔符

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2; // 验证码有效时间为2分钟

    /**
     * 令牌
     */
    public static final String TOKEN = "token"; // 请求头中token的键名

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer "; // Authorization请求头中token的前缀

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key"; // Redis中登录用户的key前缀

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid"; // JWT中存储用户ID的键名

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = Claims.SUBJECT; // JWT中存储用户名的键名，使用JWT标准的subject

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar"; // JWT中存储用户头像的键名

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created"; // JWT中存储创建时间的键名

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities"; // JWT中存储用户权限的键名

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile"; // 本地资源访问路径前缀

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi:"; // RMI远程调用协议前缀

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap:"; // LDAP远程调用协议前缀

    /**
     * LDAPS 远程方法调用
     */
    public static final String LOOKUP_LDAPS = "ldaps:"; // LDAPS远程调用协议前缀

    /**
     * 自动识别json对象白名单配置（仅允许解析的包名，范围越小越安全）
     */
    public static final String[] JSON_WHITELIST_STR = { "org.springframework", "com.debuff" }; // JSON反序列化白名单包名

    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = { "com.debuff" }; // 定时任务允许访问的包名白名单

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.debuff.common.utils.file", "com.debuff.common.config" }; // 定时任务中禁止使用的危险类名
}
