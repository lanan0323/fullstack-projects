// 包声明
package com.debuff.common.utils.spring;

// 导入Spring AOP、BeanFactory、ApplicationContext等类
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.debuff.common.utils.StringUtils;

// Spring工具类，方便在非Spring管理的环境中获取Bean
@Component
public final class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware 
{
    /** Spring应用上下文环境 */
    // Bean工厂
    private static ConfigurableListableBeanFactory beanFactory;

    // Spring应用上下文
    private static ApplicationContext applicationContext;

    // BeanFactoryPostProcessor回调，保存BeanFactory引用
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException 
    {
        SpringUtils.beanFactory = beanFactory;
    }

    // ApplicationContextAware回调，保存ApplicationContext引用
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException 
    {
        SpringUtils.applicationContext = applicationContext;
    }

    // 根据Bean名称获取Bean实例
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException
    {
        return (T) beanFactory.getBean(name);
    }

    // 根据Bean类型获取Bean实例
    public static <T> T getBean(Class<T> clz) throws BeansException
    {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    // 判断是否包含指定名称的Bean定义
    public static boolean containsBean(String name)
    {
        return beanFactory.containsBean(name);
    }

    // 判断指定名称的Bean是否为单例
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.isSingleton(name);
    }

    // 获取指定名称Bean的注册类型
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getType(name);
    }

    // 获取指定名称Bean的所有别名
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getAliases(name);
    }

    // 获取AOP代理对象
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker)
    {
        return (T) AopContext.currentProxy();
    }

    // 获取当前激活的环境配置数组
    public static String[] getActiveProfiles()
    {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    // 获取当前激活的环境配置（只取第一个）
    public static String getActiveProfile()
    {
        final String[] activeProfiles = getActiveProfiles();
        return StringUtils.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }

    // 获取配置文件中的指定属性值
    public static String getRequiredProperty(String key)
    {
        return applicationContext.getEnvironment().getRequiredProperty(key);
    }
}