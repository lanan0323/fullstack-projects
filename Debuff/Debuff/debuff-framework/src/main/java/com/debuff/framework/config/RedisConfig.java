// 包声明
package com.debuff.framework.config;

// 导入所需类
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis配置
 * 
 * @author ruoyi
 */
@Configuration // 标记为配置类
@EnableCaching // 开启Spring Cache缓存支持
public class RedisConfig extends CachingConfigurerSupport // 继承缓存配置支持类
{
    @Bean // 注册RedisTemplate Bean
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory)
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>(); // 创建RedisTemplate实例
        template.setConnectionFactory(connectionFactory); // 设置Redis连接工厂

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class); // 创建FastJson序列化器

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer()); // 设置key的序列化方式为String
        template.setValueSerializer(serializer); // 设置value的序列化方式为FastJson

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer()); // 设置Hash key的序列化方式为String
        template.setHashValueSerializer(serializer); // 设置Hash value的序列化方式为FastJson

        template.afterPropertiesSet(); // 初始化模板
        return template; // 返回配置好的RedisTemplate
    }

    @Bean // 注册限流Lua脚本Bean
    public DefaultRedisScript<Long> limitScript()
    {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(); // 创建Redis脚本实例
        redisScript.setScriptText(limitScriptText()); // 设置Lua限流脚本内容
        redisScript.setResultType(Long.class); // 设置脚本返回类型为Long
        return redisScript; // 返回脚本对象
    }

    /**
     * 限流脚本
     */
    private String limitScriptText() // 返回Lua限流脚本的文本内容
    {
        return "local key = KEYS[1]\n" + // 获取缓存key
                "local count = tonumber(ARGV[1])\n" + // 获取限流阈值
                "local time = tonumber(ARGV[2])\n" + // 获取时间窗口
                "local current = redis.call('get', key);\n" + // 获取当前访问次数
                "if current and tonumber(current) > count then\n" + // 如果当前次数超过阈值
                "    return tonumber(current);\n" + // 返回当前次数（表示被限流）
                "end\n" +
                "current = redis.call('incr', key)\n" + // 否则访问次数加1
                "if tonumber(current) == 1 then\n" + // 如果是第一次访问
                "    redis.call('expire', key, time)\n" + // 设置key的过期时间
                "end\n" +
                "return tonumber(current);"; // 返回当前访问次数
    }
}
