// 包声明
package com.debuff.common.utils.bean;

// 导入JSR303验证相关类
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

// Bean对象属性验证工具类
public class BeanValidators
{
    // 使用Validator验证对象，有违规则抛出异常
    public static void validateWithException(Validator validator, Object object, Class<?>... groups)
            throws ConstraintViolationException
    {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups); // 执行验证
        if (!constraintViolations.isEmpty()) // 有验证违规
        {
            throw new ConstraintViolationException(constraintViolations); // 抛出违规异常
        }
    }
}