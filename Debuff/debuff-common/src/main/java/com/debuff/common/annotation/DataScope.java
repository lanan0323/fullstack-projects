// 包声明：数据权限过滤注解所在包
package com.debuff.common.annotation;

// 导入Java注解相关类
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解
 * 
 * @author ruoyi
 */
@Target(ElementType.METHOD) // 注解作用于方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，可通过反射获取
@Documented // 包含在JavaDoc中
public @interface DataScope
{
    /**
     * 部门表的别名
     */
    public String deptAlias() default ""; // 部门表别名，默认为空

    /**
     * 用户表的别名
     */
    public String userAlias() default ""; // 用户表别名，默认为空

    /**
     * 权限字符（用于多个角色匹配符合要求的权限）默认根据权限注解@ss获取，多个权限用逗号分隔开来
     */
    public String permission() default ""; // 权限字符，默认为空
}
