// 包声明
package com.debuff.framework.config.properties;

// 导入所需类
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * druid 配置属性
 * 
 * @author ruoyi
 */
@Configuration // 标记为配置类
public class DruidProperties
{
    @Value("${spring.datasource.druid.initialSize}") // 从配置文件读取初始连接数
    private int initialSize;

    @Value("${spring.datasource.druid.minIdle}") // 从配置文件读取最小空闲连接数
    private int minIdle;

    @Value("${spring.datasource.druid.maxActive}") // 从配置文件读取最大活跃连接数
    private int maxActive;

    @Value("${spring.datasource.druid.maxWait}") // 从配置文件读取获取连接最大等待时间
    private int maxWait;

    @Value("${spring.datasource.druid.connectTimeout}") // 从配置文件读取连接超时时间
    private int connectTimeout;

    @Value("${spring.datasource.druid.socketTimeout}") // 从配置文件读取Socket超时时间
    private int socketTimeout;

    @Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis}") // 从配置文件读取空闲连接检测间隔
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.druid.minEvictableIdleTimeMillis}") // 从配置文件读取连接最小空闲存活时间
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.maxEvictableIdleTimeMillis}") // 从配置文件读取连接最大空闲存活时间
    private int maxEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.validationQuery}") // 从配置文件读取连接有效性检测SQL
    private String validationQuery;

    @Value("${spring.datasource.druid.testWhileIdle}") // 从配置文件读取空闲时是否检测连接有效性
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.testOnBorrow}") // 从配置文件读取获取连接时是否检测有效性
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.testOnReturn}") // 从配置文件读取归还连接时是否检测有效性
    private boolean testOnReturn;

    public DruidDataSource dataSource(DruidDataSource datasource) // 将配置属性应用到Druid数据源
    {
        /** 配置初始化大小、最小、最大 */
        datasource.setInitialSize(initialSize); // 设置初始连接数
        datasource.setMaxActive(maxActive); // 设置最大活跃连接数
        datasource.setMinIdle(minIdle); // 设置最小空闲连接数

        /** 配置获取连接等待超时的时间 */
        datasource.setMaxWait(maxWait); // 设置获取连接最大等待时间
        
        /** 配置驱动连接超时时间，检测数据库建立连接的超时时间，单位是毫秒 */
        datasource.setConnectTimeout(connectTimeout); // 设置连接超时时间
        
        /** 配置网络超时时间，等待数据库操作完成的网络超时时间，单位是毫秒 */
        datasource.setSocketTimeout(socketTimeout); // 设置Socket超时时间

        /** 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis); // 设置空闲连接检测间隔

        /** 配置一个连接在池中最小、最大生存的时间，单位是毫秒 */
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis); // 设置连接最小空闲存活时间
        datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis); // 设置连接最大空闲存活时间

        /**
         * 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
         */
        datasource.setValidationQuery(validationQuery); // 设置连接有效性检测SQL
        /** 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 */
        datasource.setTestWhileIdle(testWhileIdle); // 设置空闲时检测连接有效性
        /** 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnBorrow(testOnBorrow); // 设置获取连接时检测有效性
        /** 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnReturn(testOnReturn); // 设置归还连接时检测有效性
        return datasource; // 返回配置好的数据源
    }
}
