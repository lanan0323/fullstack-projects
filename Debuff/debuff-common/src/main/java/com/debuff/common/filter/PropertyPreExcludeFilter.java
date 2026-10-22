// 包声明
package com.debuff.common.filter;

// 导入fastjson2的属性预过滤类
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;

/**
 * 排除JSON敏感属性
 * 
 * @author ruoyi
 */
public class PropertyPreExcludeFilter extends SimplePropertyPreFilter
{
    public PropertyPreExcludeFilter() // 空构造方法
    {
    }

    public PropertyPreExcludeFilter addExcludes(String... filters) // 添加需要排除的属性名
    {
        for (int i = 0; i < filters.length; i++) // 遍历所有要排除的属性
        {
            this.getExcludes().add(filters[i]); // 加入排除集合
        }
        return this; // 支持链式调用
    }
}
