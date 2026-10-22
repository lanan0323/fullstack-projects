// 包声明：匿名访问注解所在包
package com.debuff.common.annotation;

// 导入Java注解相关类
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 匿名访问不鉴权注解
 * 
 * @author ruoyi
 */
@Target({ ElementType.METHOD, ElementType.TYPE }) // 注解可作用于方法和类上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented // 包含在JavaDoc中
public @interface Anonymous
{
    // 匿名访问标记注解，无需额外属性，标注后该接口无需登录鉴权即可访问
}
