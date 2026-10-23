// 包声明
package com.debuff.framework.aspectj;

// 导入所需类
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.debuff.common.annotation.DataSource;
import com.debuff.common.utils.StringUtils;
import com.debuff.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 多数据源处理
 * 
 * @author ruoyi
 */
@Aspect // 声明为AOP切面
@Order(1) // 设置切面优先级为最高（最先执行）
@Component // 注册为Spring Bean
public class DataSourceAspect
{
    protected Logger logger = LoggerFactory.getLogger(getClass()); // 日志记录器

    @Pointcut("@annotation(com.debuff.common.annotation.DataSource)" // 注解在方法上时匹配
            + "|| @within(com.debuff.common.annotation.DataSource)") // 注解在类上时也匹配
    public void dsPointCut() // 定义切点，匹配所有标注了@DataSource的方法或类
    {

    }

    @Around("dsPointCut()") // 环绕通知，在切点方法执行前后进行拦截
    public Object around(ProceedingJoinPoint point) throws Throwable
    {
        DataSource dataSource = getDataSource(point); // 获取目标方法或类上的@DataSource注解

        if (StringUtils.isNotNull(dataSource)) // 如果注解存在
        {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name()); // 设置当前线程的数据源类型
        }

        try
        {
            return point.proceed(); // 执行目标方法
        }
        finally
        {
            // 销毁数据源 在执行方法之后
            DynamicDataSourceContextHolder.clearDataSourceType(); // 方法执行完毕后清除数据源，防止内存泄漏
        }
    }

    /**
     * 获取需要切换的数据源
     */
    public DataSource getDataSource(ProceedingJoinPoint point) // 从方法或类上获取@DataSource注解
    {
        MethodSignature signature = (MethodSignature) point.getSignature(); // 获取方法签名
        DataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class); // 优先从方法上查找注解
        if (Objects.nonNull(dataSource)) // 如果方法上找到了注解
        {
            return dataSource; // 直接返回
        }

        return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class); // 方法上没有则从类上查找
    }
}
