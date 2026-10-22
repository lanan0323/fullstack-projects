// 包声明
package com.debuff.framework.config;

// 导入所需类
import java.nio.charset.Charset;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import com.debuff.common.constant.Constants;

/**
 * Redis使用FastJson序列化
 * 
 * @author ruoyi
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> // 实现Redis序列化接口，使用FastJson2进行序列化
{
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8"); // 默认字符集为UTF-8

    static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(Constants.JSON_WHITELIST_STR); // JSON自动类型过滤白名单，防止反序列化漏洞

    private Class<T> clazz; // 泛型类型

    public FastJson2JsonRedisSerializer(Class<T> clazz) // 构造函数，传入需要序列化的类型
    {
        super();
        this.clazz = clazz; // 保存类型信息
    }

    @Override
    public byte[] serialize(T t) throws SerializationException // 将对象序列化为字节数组
    {
        if (t == null) // 如果对象为空
        {
            return new byte[0]; // 返回空字节数组
        }
        return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(DEFAULT_CHARSET); // 序列化为JSON字符串（包含类名信息），转为字节数组
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException // 将字节数组反序列化为对象
    {
        if (bytes == null || bytes.length <= 0) // 如果字节数组为空
        {
            return null; // 返回null
        }
        String str = new String(bytes, DEFAULT_CHARSET); // 将字节数组转为字符串

        return JSON.parseObject(str, clazz, AUTO_TYPE_FILTER); // 使用白名单过滤器反序列化为对象
    }
}
