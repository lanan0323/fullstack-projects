// 包声明：Redis缓存工具所在包
package com.debuff.common.core.redis;

// 导入集合相关类
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
// 导入Spring自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Redis操作类
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
// 导入Spring组件注解
import org.springframework.stereotype.Component;

/**
 * spring redis 工具类
 *
 * @author ruoyi
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" }) // 抑制未检查类型转换警告
@Component // 注册为Spring组件
public class RedisCache // Redis缓存工具类
{
    @Autowired // 自动注入RedisTemplate
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     */
    public <T> void setCacheObject(final String key, final T value) // 缓存对象（无过期时间）
    {
        redisTemplate.opsForValue().set(key, value); // 使用ValueOperations设置值
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) // 缓存对象并设置过期时间
    {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit); // 设置值和过期时间
    }

    /**
     * 设置有效时间
     */
    public boolean expire(final String key, final long timeout) // 设置过期时间（秒）
    {
        return expire(key, timeout, TimeUnit.SECONDS); // 调用带时间单位的方法
    }

    /**
     * 设置有效时间
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) // 设置过期时间（指定时间单位）
    {
        return redisTemplate.expire(key, timeout, unit); // 设置key的过期时间
    }

    /**
     * 获取有效时间
     */
    public long getExpire(final String key) // 获取key的剩余过期时间
    {
        return redisTemplate.getExpire(key); // 返回剩余时间
    }

    /**
     * 判断 key是否存在
     */
    public Boolean hasKey(String key) // 判断指定key是否存在
    {
        return redisTemplate.hasKey(key); // 返回是否存在
    }

    /**
     * 获得缓存的基本对象。
     */
    public <T> T getCacheObject(final String key) // 获取缓存的基本对象
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue(); // 获取ValueOperations
        return operation.get(key); // 返回缓存值
    }

    /**
     * 删除单个对象
     */
    public boolean deleteObject(final String key) // 删除指定key的缓存
    {
        return redisTemplate.delete(key); // 删除并返回是否成功
    }

    /**
     * 删除集合对象
     */
    public boolean deleteObject(final Collection collection) // 批量删除缓存
    {
        return redisTemplate.delete(collection) > 0; // 删除多个key，返回是否至少删除了一个
    }

    /**
     * 缓存List数据
     */
    public <T> long setCacheList(final String key, final List<T> dataList) // 缓存List数据
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList); // 将列表数据从右端批量推入
        return count == null ? 0 : count; // 返回存入的数量
    }

    /**
     * 获得缓存的list对象
     */
    public <T> List<T> getCacheList(final String key) // 获取缓存的List数据
    {
        return redisTemplate.opsForList().range(key, 0, -1); // 获取全部列表数据
    }

    /**
     * 缓存Set
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) // 缓存Set数据
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key); // 获取Set操作对象
        Iterator<T> it = dataSet.iterator(); // 遍历数据集合
        while (it.hasNext()) // 逐个添加
        {
            setOperation.add(it.next()); // 添加到Redis Set中
        }
        return setOperation; // 返回Set操作对象
    }

    /**
     * 获得缓存的set
     */
    public <T> Set<T> getCacheSet(final String key) // 获取缓存的Set数据
    {
        return redisTemplate.opsForSet().members(key); // 返回Set中所有成员
    }

    /**
     * 缓存Map
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) // 缓存Map数据
    {
        if (dataMap != null) { // 非空检查
            redisTemplate.opsForHash().putAll(key, dataMap); // 批量写入Hash
        }
    }

    /**
     * 获得缓存的Map
     */
    public <T> Map<String, T> getCacheMap(final String key) // 获取缓存的Map数据
    {
        return redisTemplate.opsForHash().entries(key); // 返回Hash中所有键值对
    }

    /**
     * 往Hash中存入数据
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) // 向Hash中存入单个字段
    {
        redisTemplate.opsForHash().put(key, hKey, value); // 设置Hash字段值
    }

    /**
     * 获取Hash中的数据
     */
    public <T> T getCacheMapValue(final String key, final String hKey) // 获取Hash中指定字段的值
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash(); // 获取Hash操作对象
        return opsForHash.get(key, hKey); // 返回指定字段的值
    }

    /**
     * 获取多个Hash中的数据
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) // 批量获取Hash中多个字段的值
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys); // 返回多个字段值列表
    }

    /**
     * 删除Hash中的某条数据
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) // 删除Hash中指定字段
    {
        return redisTemplate.opsForHash().delete(key, hKey) > 0; // 删除并返回是否成功
    }

    /**
     * 获得缓存的基本对象列表
     */
    public Collection<String> keys(final String pattern) // 根据模式匹配获取所有key
    {
        return redisTemplate.keys(pattern); // 返回匹配的key集合
    }
}
