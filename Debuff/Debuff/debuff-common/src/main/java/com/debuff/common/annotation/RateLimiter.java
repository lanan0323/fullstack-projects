// 包声明：限流注解所在包
package com.debuff.common.annotation;

// 导入Java注解相关类
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
// 导入缓存常量，获取默认限流key
import com.debuff.common.constant.CacheConstants;
// 导入限流类型枚举
import com.debuff.common.enums.LimitType;

/**
 * 限流注解
 * 
 * @author ruoyi
 */
@Target(ElementType.METHOD) // 注解作用于方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented // 包含在JavaDoc中
public @interface RateLimiter
{
    /**
     * 限流key
     */
    public String key() default CacheConstants.RATE_LIMIT_KEY; // 限流的缓存键名，默认使用常量

    /**
     * 限流时间,单位秒
     */
    public int time() default 60; // 限流时间窗口，默认60秒

    /**
     * 限流次数
     */
    public int count() default 100; // 时间窗口内允许的最大请求次数，默认100次

    /**
     * 限流类型
     */
    public LimitType limitType() default LimitType.DEFAULT; // 限流策略类型，默认按全局限流
}
