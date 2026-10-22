// 包声明：Excel注解集所在包
package com.debuff.common.annotation;

// 导入Java注解相关类
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel注解集
 * 
 * @author ruoyi
 */
@Target(ElementType.FIELD) // 注解作用于字段上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
public @interface Excels
{
    public Excel[] value(); // 包含多个Excel注解的数组，支持一个字段映射多个Excel列
}
