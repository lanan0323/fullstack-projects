// 包声明
package com.debuff.framework.datasource;

// 导入所需类
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * 
 * @author ruoyi
 */
public class DynamicDataSource extends AbstractRoutingDataSource // 继承Spring的抽象路由数据源，实现动态切换数据源
{
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) // 构造函数，传入默认数据源和所有数据源映射
    {
        super.setDefaultTargetDataSource(defaultTargetDataSource); // 设置默认数据源
        super.setTargetDataSources(targetDataSources); // 设置所有目标数据源映射
        super.afterPropertiesSet(); // 初始化数据源
    }

    @Override
    protected Object determineCurrentLookupKey() // 决定当前使用哪个数据源的key
    {
        return DynamicDataSourceContextHolder.getDataSourceType(); // 从线程本地变量中获取当前数据源类型
    }
}
