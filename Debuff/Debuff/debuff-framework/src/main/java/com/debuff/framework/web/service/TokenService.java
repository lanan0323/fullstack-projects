// 包声明
package com.debuff.framework.web.service;

// 导入所需类
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.utils.ServletUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.ip.AddressUtils;
import com.debuff.common.utils.ip.IpUtils;
import com.debuff.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * token验证处理
 *
 * @author ruoyi
 */
@Component
public class TokenService
{
    private static final Logger log = LoggerFactory.getLogger(TokenService.class); // 日志对象

    // 令牌自定义标识
    @Value("${token.header}")
    private String header; // 请求头中token的字段名

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret; // JWT签名密钥

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime; // token过期时间(分钟)

    protected static final long MILLIS_SECOND = 1000; // 1秒的毫秒数

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND; // 1分钟的毫秒数

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L; // 20分钟的毫秒数(刷新阈值)

    @Autowired
    private RedisCache redisCache; // Redis缓存，存储loginUser信息

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = getToken(request); // 从请求头中提取token
        if (StringUtils.isNotEmpty(token)) // token不为空
        {
            try
            {
                Claims claims = parseToken(token); // 解析JWT获取声明数据
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY); // 从JWT中取出uuid
                String userKey = getTokenKey(uuid); // 拼接Redis中loginUser的key
                LoginUser user = redisCache.getCacheObject(userKey); // 从Redis获取登录用户信息
                return user;
            }
            catch (Exception e) // 解析异常
            {
                log.error("获取用户信息异常'{}'", e.getMessage()); // 记录错误日志
            }
        }
        return null; // 未登录或token无效
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser)
    {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) // 登录用户对象和token都不为空
        {
            refreshToken(loginUser); // 刷新token有效期
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token)) // token不为空
        {
            String userKey = getTokenKey(token); // 拼接Redis的key
            redisCache.deleteObject(userKey); // 从Redis删除登录用户信息
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser)
    {
        String token = IdUtils.fastUUID(); // 生成唯一uuid作为Redis缓存的key
        loginUser.setToken(token); // 将uuid设置到登录用户对象
        setUserAgent(loginUser); // 设置用户代理信息(浏览器、操作系统、IP等)
        refreshToken(loginUser); // 将loginUser缓存到Redis

        Map<String, Object> claims = new HashMap<>(); // JWT声明数据
        claims.put(Constants.LOGIN_USER_KEY, token); // 将uuid放入JWT声明中
        return createToken(claims); // 生成并返回JWT字符串
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime(); // 获取token过期时间戳
        long currentTime = System.currentTimeMillis(); // 获取当前时间戳
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) // 剩余有效期不足20分钟
        {
            refreshToken(loginUser); // 自动刷新token有效期
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis()); // 更新登录时间戳为当前时间
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE); // 计算新的过期时间
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken()); // 拼接Redis的key
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES); // 存入Redis并设置过期时间
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent")); // 解析请求的User-Agent
        String ip = IpUtils.getIpAddr(); // 获取客户端IP
        loginUser.setIpaddr(ip); // 设置登录IP
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip)); // 根据IP解析登录地址
        loginUser.setBrowser(userAgent.getBrowser().getName()); // 设置浏览器名称
        loginUser.setOs(userAgent.getOperatingSystem().getName()); // 设置操作系统名称
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder() // 构建JWT
                .setClaims(claims) // 设置声明数据
                .signWith(SignatureAlgorithm.HS512, secret).compact(); // 使用HS512算法和密钥签名
        return token; // 返回JWT字符串
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser() // 创建JWT解析器
                .setSigningKey(secret) // 设置签名密钥
                .parseClaimsJws(token) // 解析JWT
                .getBody(); // 获取声明体
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token); // 解析JWT获取声明
        return claims.getSubject(); // 返回主题(用户名)
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(header); // 从请求头获取token
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) // token以"Bearer "开头
        {
            token = token.replace(Constants.TOKEN_PREFIX, ""); // 去掉"Bearer "前缀
        }
        return token; // 返回纯token
    }

    private String getTokenKey(String uuid)
    {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid; // 拼接Redis缓存key: login_tokens:{uuid}
    }
}
