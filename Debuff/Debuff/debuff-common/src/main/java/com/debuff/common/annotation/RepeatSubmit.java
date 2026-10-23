// 包声明：防重复提交注解所在包
package com.debuff.common.annotation;

// 导入Java注解相关类
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解防止表单重复提交
 * 
 * @author ruoyi
 *
 */
@Inherited // 子类可继承父类注解
@Target(ElementType.METHOD) // 注解作用于方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented // 包含在JavaDoc中
public @interface RepeatSubmit
{
    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     */
    public int interval() default 5000; // 防重复提交的时间间隔，默认5000毫秒

    /**
     * 提示消息
     */
    public String message() default "不允许重复提交，请稍候再试"; // 重复提交时的提示信息
}
