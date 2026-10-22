// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入RuoYi的字符串工具类
import com.debuff.common.utils.StringUtils;

/**
 * 缓存信息
 * 
 * @author ruoyi
 */
public class SysCache // 缓存信息实体类，用于封装Redis缓存的展示数据
{
    /** 缓存名称 */
    private String cacheName = ""; // 缓存名称，默认为空字符串

    /** 缓存键名 */
    private String cacheKey = ""; // 缓存键名，默认为空字符串

    /** 缓存内容 */
    private String cacheValue = ""; // 缓存值，默认为空字符串

    /** 备注 */
    private String remark = ""; // 备注信息，默认为空字符串

    public SysCache() // 无参构造方法
    {

    }

    public SysCache(String cacheName, String remark) // 通过缓存名称和备注创建缓存对象
    {
        this.cacheName = cacheName; // 赋值缓存名称
        this.remark = remark; // 赋值备注
    }

    public SysCache(String cacheName, String cacheKey, String cacheValue) // 通过名称、键名和值创建缓存对象
    {
        this.cacheName = StringUtils.replace(cacheName, ":", ""); // 去除缓存名称中的冒号
        this.cacheKey = StringUtils.replace(cacheKey, cacheName, ""); // 从键名中去除缓存名称前缀，得到纯键名
        this.cacheValue = cacheValue; // 赋值缓存值
    }

    public String getCacheName() // 获取缓存名称
    {
        return cacheName;
    }

    public void setCacheName(String cacheName) // 设置缓存名称
    {
        this.cacheName = cacheName;
    }

    public String getCacheKey() // 获取缓存键名
    {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) // 设置缓存键名
    {
        this.cacheKey = cacheKey;
    }

    public String getCacheValue() // 获取缓存值
    {
        return cacheValue;
    }

    public void setCacheValue(String cacheValue) // 设置缓存值
    {
        this.cacheValue = cacheValue;
    }

    public String getRemark() // 获取备注
    {
        return remark;
    }

    public void setRemark(String remark) // 设置备注
    {
        this.remark = remark;
    }
}
