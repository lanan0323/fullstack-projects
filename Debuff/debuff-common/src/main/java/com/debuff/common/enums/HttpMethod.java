// 包声明
package com.debuff.common.enums;

// 导入HashMap和Map用于存储请求方法映射
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;

/**
 * 请求方式
 *
 * @author ruoyi
 */
public enum HttpMethod
{
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE; // 枚举所有HTTP请求方法

    private static final Map<String, HttpMethod> mappings = new HashMap<>(16); // 名称到枚举的映射表

    static
    {
        for (HttpMethod httpMethod : values()) // 遍历所有枚举值
        {
            mappings.put(httpMethod.name(), httpMethod); // 存入映射表
        }
    }

    @Nullable
    public static HttpMethod resolve(@Nullable String method) // 根据名称解析HTTP方法
    {
        return (method != null ? mappings.get(method) : null); // 从映射表查找
    }

    public boolean matches(String method) // 判断当前枚举是否匹配指定方法名
    {
        return (this == resolve(method)); // 比较解析结果
    }
}
