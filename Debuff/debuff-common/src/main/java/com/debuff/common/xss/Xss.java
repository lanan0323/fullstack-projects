// 包声明
package com.debuff.common.xss;

// 导入JSR303自定义约束注解相关类
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义XSS校验注解，标注在字段上进行XSS过滤验证
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Target(value = { ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER }) // 可用在方法/字段/构造器/参数上
@Constraint(validatedBy = { XssValidator.class }) // 指定验证器为XssValidator
public @interface Xss
{
    // 验证失败的默认提示信息
    String message()

    default "不允许任何脚本运行";

    // 验证分组（默认空）
    Class<?>[] groups() default {};

    // 负载类型（默认空）
    Class<? extends Payload>[] payload() default {};
}