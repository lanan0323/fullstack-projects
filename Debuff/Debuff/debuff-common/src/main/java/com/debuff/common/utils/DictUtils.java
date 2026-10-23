// 包声明
package com.debuff.common.utils;

// 导入集合、JSON、Redis缓存和字典相关类
import java.util.Collection;
import java.util.List;
import com.alibaba.fastjson2.JSONArray;
import com.debuff.common.constant.CacheConstants;
import com.debuff.common.core.domain.entity.SysDictData;
import com.debuff.common.core.redis.RedisCache;
import com.debuff.common.utils.spring.SpringUtils;

/**
 * 字典工具类
 * 
 * @author ruoyi
 */
public class DictUtils
{
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ","; // 默认分隔符

    /**
     * 设置字典缓存
     * 
     * @param key 参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas) // 将字典数据存入Redis缓存
    {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     * 
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) // 从Redis获取字典缓存数据
    {
        JSONArray arrayCache = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(arrayCache)) // 缓存存在时转为字典数据列表
        {
            return arrayCache.toList(SysDictData.class);
        }
        return null;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     * 
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue) // 根据字典类型和值获取标签(默认逗号分隔)
    {
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     * 
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel) // 根据字典类型和标签获取值(默认逗号分隔)
    {
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     * 
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue, String separator) // 根据字典值反查字典标签
    {
        StringBuilder propertyString = new StringBuilder(); // 拼接多值标签
        List<SysDictData> datas = getDictCache(dictType); // 获取字典缓存

        if (StringUtils.isNotNull(datas)) // 缓存数据存在
        {
            if (StringUtils.containsAny(separator, dictValue)) // 包含分隔符说明是多值
            {
                for (SysDictData dict : datas) // 遍历字典数据
                {
                    for (String value : dictValue.split(separator)) // 遍历每个字典值
                    {
                        if (value.equals(dict.getDictValue())) // 匹配到字典值
                        {
                            propertyString.append(dict.getDictLabel()).append(separator); // 拼接标签
                            break;
                        }
                    }
                }
            }
            else // 单值直接查找
            {
                for (SysDictData dict : datas)
                {
                    if (dictValue.equals(dict.getDictValue())) // 匹配字典值
                    {
                        return dict.getDictLabel(); // 返回对应标签
                    }
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator); // 去除末尾分隔符
    }

    /**
     * 根据字典类型和字典标签获取字典值
     * 
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel, String separator) // 根据字典标签反查字典值
    {
        StringBuilder propertyString = new StringBuilder(); // 拼接多值
        List<SysDictData> datas = getDictCache(dictType); // 获取字典缓存

        if (StringUtils.containsAny(separator, dictLabel) && StringUtils.isNotEmpty(datas)) // 多值情况
        {
            for (SysDictData dict : datas) // 遍历字典数据
            {
                for (String label : dictLabel.split(separator)) // 遍历每个标签
                {
                    if (label.equals(dict.getDictLabel())) // 匹配标签
                    {
                        propertyString.append(dict.getDictValue()).append(separator); // 拼接值
                        break;
                    }
                }
            }
        }
        else // 单值直接查找
        {
            for (SysDictData dict : datas)
            {
                if (dictLabel.equals(dict.getDictLabel())) // 匹配标签
                {
                    return dict.getDictValue(); // 返回对应值
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator); // 去除末尾分隔符
    }

    /**
     * 删除指定字典缓存
     * 
     * @param key 字典键
     */
    public static void removeDictCache(String key) // 删除指定key的字典缓存
    {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() // 清空所有字典缓存
    {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(CacheConstants.SYS_DICT_KEY + "*"); // 查找所有字典缓存key
        SpringUtils.getBean(RedisCache.class).deleteObject(keys); // 批量删除
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) // 拼接字典缓存的Redis key
    {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }
}
