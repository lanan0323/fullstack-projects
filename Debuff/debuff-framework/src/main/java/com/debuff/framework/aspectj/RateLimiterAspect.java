// 包声明
package com.debuff.framework.aspectj;

// 导入所需类
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import com.debuff.common.annotation.RateLimiter;
import com.debuff.common.enums.LimitType;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.ip.IpUtils;

/**
 * 限流处理
 *
 * @author ruoyi
 */
@Aspect // 声明为AOP切面
@Component // 注册为Spring Bean
public class RateLimiterAspect
{
    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class); // 日志记录器

    private RedisTemplate<Object, Object> redisTemplate; // Redis操作模板

    private RedisScript<Long> limitScript; // Lua限流脚本

    @Autowired // 自动注入RedisTemplate
    public void setRedisTemplate1(RedisTemplate<Object, Object> redisTemplate)
    {
        this.redisTemplate = redisTemplate; // 设置Redis模板
    }

    @Autowired // 自动注入限流Lua脚本
    public void setLimitScript(RedisScript<Long> limitScript)
    {
        this.limitScript = limitScript; // 设置限流脚本
    }

    @Before("@annotation(rateLimiter)") // 前置通知，拦截标注了@RateLimiter的方法
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) throws Throwable
    {
        int time = rateLimiter.time(); // 获取限流时间窗口（秒）
        int count = rateLimiter.count(); // 获取限流次数阈值

        String combineKey = getCombineKey(rateLimiter, point); // 生成限流缓存key
        List<Object> keys = Collections.singletonList(combineKey); // 将key封装为Lua脚本所需的列表
        try
        {
            Long number = redisTemplate.execute(limitScript, keys, count, time); // 执行Lua脚本，返回当前访问次数
            if (StringUtils.isNull(number) || number.intValue() > count) // 如果超过限流阈值
            {
                throw new ServiceException("访问过于频繁，请稍候再试"); // 抛出限流异常
            }
            log.info("限制请求'{}',当前请求'{}',缓存key'{}'", count, number.intValue(), combineKey); // 记录限流日志
        }
        catch (ServiceException e) // 业务异常直接抛出
        {
            throw e;
        }
        catch (Exception e) // 其他异常包装为运行时异常
        {
            throw new RuntimeException("服务器限流异常，请稍候再试"); // 限流组件异常时的友好提示
        }
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point) // 根据限流类型生成组合缓存key
    {
        StringBuffer stringBuffer = new StringBuffer(rateLimiter.key()); // 以注解配置的key为前缀
        if (rateLimiter.limitType() == LimitType.IP) // 如果是按IP限流
        {
            stringBuffer.append(IpUtils.getIpAddr()).append("-"); // 拼接客户端IP地址
        }
        MethodSignature signature = (MethodSignature) point.getSignature(); // 获取方法签名
        Method method = signature.getMethod(); // 获取目标方法
        Class<?> targetClass = method.getDeclaringClass(); // 获取目标类
        stringBuffer.append(targetClass.getName()).append("-").append(method.getName()); // 拼接类名和方法名
        return stringBuffer.toString(); // 返回完整的限流key
    }
}
