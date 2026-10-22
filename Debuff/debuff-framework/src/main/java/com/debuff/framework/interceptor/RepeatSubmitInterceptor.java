// 包声明
package com.debuff.framework.interceptor;

// 导入所需类
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.alibaba.fastjson2.JSON;
import com.debuff.common.annotation.RepeatSubmit;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.utils.ServletUtils;

/**
 * 防止重复提交拦截器
 *
 * @author ruoyi
 */
@Component // 注册为Spring Bean
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor // 抽象拦截器，实现HandlerInterceptor接口
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception // 请求处理前执行
    {
        if (handler instanceof HandlerMethod) // 如果处理器是Controller方法
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler; // 转换为HandlerMethod
            Method method = handlerMethod.getMethod(); // 获取方法对象
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class); // 获取方法上的@RepeatSubmit注解
            if (annotation != null) // 如果存在防重复提交注解
            {
                if (this.isRepeatSubmit(request, annotation)) // 调用子类实现判断是否重复提交
                {
                    AjaxResult ajaxResult = AjaxResult.error(annotation.message()); // 构建错误响应
                    ServletUtils.renderString(response, JSON.toJSONString(ajaxResult)); // 返回错误信息给前端
                    return false; // 拦截请求，不继续执行
                }
            }
            return true; // 不是重复提交，放行
        }
        else
        {
            return true; // 非Controller方法，直接放行
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request 请求信息
     * @param annotation 防重复注解参数
     * @return 结果
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation); // 抽象方法，由子类实现具体的防重复提交规则
}
