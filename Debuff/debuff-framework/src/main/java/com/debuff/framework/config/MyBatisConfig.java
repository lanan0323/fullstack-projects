// 包声明
package com.debuff.framework.config;

// 导入所需类
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import com.debuff.common.utils.StringUtils;

/**
 * Mybatis支持*匹配扫描包
 * 
 * @author ruoyi
 */
@Configuration // 标记为配置类
public class MyBatisConfig
{
    @Autowired // 自动注入环境变量，用于读取配置属性
    private Environment env;

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class"; // 默认的资源匹配模式，扫描所有class文件

    public static String setTypeAliasesPackage(String typeAliasesPackage) // 解析类型别名包路径，支持通配符*匹配多个包
    {
        ResourcePatternResolver resolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver(); // 创建资源模式解析器
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver); // 创建带缓存的元数据读取工厂
        List<String> allResult = new ArrayList<String>(); // 存储所有扫描到的包名
        try
        {
            for (String aliasesPackage : typeAliasesPackage.split(",")) // 遍历逗号分隔的包路径
            {
                List<String> result = new ArrayList<String>(); // 当前包路径的扫描结果
                aliasesPackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(aliasesPackage.trim()) + "/" + DEFAULT_RESOURCE_PATTERN; // 拼接完整的资源路径模式
                Resource[] resources = resolver.getResources(aliasesPackage); // 扫描匹配的资源
                if (resources != null && resources.length > 0) // 如果扫描到了资源
                {
                    MetadataReader metadataReader = null; // 元数据读取器
                    for (Resource resource : resources) // 遍历每个资源
                    {
                        if (resource.isReadable()) // 如果资源可读
                        {
                            metadataReader = metadataReaderFactory.getMetadataReader(resource); // 获取类的元数据
                            try
                            {
                                result.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName()); // 获取类所在包名并添加到结果列表
                            }
                            catch (ClassNotFoundException e) // 类加载失败
                            {
                                e.printStackTrace(); // 打印异常堆栈
                            }
                        }
                    }
                }
                if (result.size() > 0) // 如果有扫描结果
                {
                    HashSet<String> hashResult = new HashSet<String>(result); // 使用HashSet去重
                    allResult.addAll(hashResult); // 添加到总结果列表
                }
            }
            if (allResult.size() > 0) // 如果扫描到了包
            {
                typeAliasesPackage = String.join(",", (String[]) allResult.toArray(new String[0])); // 将所有包名用逗号拼接
            }
            else
            {
                throw new RuntimeException("mybatis typeAliasesPackage 路径扫描错误,参数typeAliasesPackage:" + typeAliasesPackage + "未找到任何包"); // 未扫描到任何包时抛出异常
            }
        }
        catch (IOException e) // IO异常
        {
            e.printStackTrace(); // 打印异常堆栈
        }
        return typeAliasesPackage; // 返回解析后的包名字符串
    }

    public Resource[] resolveMapperLocations(String[] mapperLocations) // 解析Mapper XML文件位置，支持通配符
    {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(); // 创建资源解析器
        List<Resource> resources = new ArrayList<Resource>(); // 存储解析到的资源
        if (mapperLocations != null) // 如果配置了Mapper位置
        {
            for (String mapperLocation : mapperLocations) // 遍历每个Mapper位置
            {
                try
                {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation); // 解析匹配的资源
                    resources.addAll(Arrays.asList(mappers)); // 添加到资源列表
                }
                catch (IOException e) // 解析失败时忽略
                {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]); // 返回资源数组
    }

    @Bean // 注册SqlSessionFactory Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception // 创建MyBatis的SqlSessionFactory
    {
        String typeAliasesPackage = env.getProperty("mybatis.typeAliasesPackage"); // 读取类型别名包配置
        String mapperLocations = env.getProperty("mybatis.mapperLocations"); // 读取Mapper XML位置配置
        String configLocation = env.getProperty("mybatis.configLocation"); // 读取MyBatis配置文件位置
        typeAliasesPackage = setTypeAliasesPackage(typeAliasesPackage); // 解析类型别名包路径
        VFS.addImplClass(SpringBootVFS.class); // 添加Spring Boot的VFS实现

        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean(); // 创建SqlSessionFactoryBean
        sessionFactory.setDataSource(dataSource); // 设置数据源
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage); // 设置类型别名包
        sessionFactory.setMapperLocations(resolveMapperLocations(StringUtils.split(mapperLocations, ","))); // 设置Mapper XML位置
        sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(configLocation)); // 设置MyBatis配置文件位置
        return sessionFactory.getObject(); // 返回SqlSessionFactory实例
    }
}
