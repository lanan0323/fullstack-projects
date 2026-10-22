// 包声明：操作日志注解所在包
package com.debuff.common.annotation;

// 导入Java注解相关类
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
// 导入业务类型枚举和数据源类型枚举
import com.debuff.common.enums.BusinessType;
import com.debuff.common.enums.OperatorType;

/**
 * 自定义操作日志记录注解
 * 
 * @author ruoyi
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD }) // 注解可作用于参数和方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented // 包含在JavaDoc中
public @interface Log
{
    /**
     * 模块
     */
    public String title() default ""; // 日志标题（模块名称），默认为空

    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER; // 业务操作类型，默认为OTHER

    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE; // 操作者类型，默认为后台管理员

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true; // 是否保存请求入参，默认保存

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true; // 是否保存响应出参，默认保存

    /**
     * 排除指定的请求参数
     */
    public String[] excludeParamNames() default {}; // 需要排除的请求参数名列表，默认不排除
}
