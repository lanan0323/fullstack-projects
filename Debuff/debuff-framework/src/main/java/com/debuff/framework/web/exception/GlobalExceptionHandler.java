// 包声明
package com.debuff.framework.web.exception;

// 导入所需类
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.debuff.common.constant.HttpStatus;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.exception.DemoModeException;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.StringUtils;

/**
 * 全局异常处理器
 * 
 * @author ruoyi
 */
@RestControllerAdvice // 全局异常处理注解，拦截所有Controller异常
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class); // 日志对象

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class) // 拦截权限不足异常
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI(); // 获取请求路径
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage()); // 记录错误日志
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权"); // 返回403错误
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // 拦截请求方法不支持异常
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request)
    {
        String requestURI = request.getRequestURI(); // 获取请求路径
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod()); // 记录错误日志
        return AjaxResult.error(e.getMessage()); // 返回错误信息
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class) // 拦截自定义业务异常
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request)
    {
        log.error(e.getMessage(), e); // 记录错误日志
        Integer code = e.getCode(); // 获取错误码
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage()); // 有错误码则带code返回
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class) // 拦截路径变量缺失异常
    public AjaxResult handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI(); // 获取请求路径
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e); // 记录错误日志
        return AjaxResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName())); // 返回缺失变量名
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class) // 拦截参数类型不匹配异常
    public AjaxResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI(); // 获取请求路径
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e); // 记录错误日志
        return AjaxResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), e.getValue())); // 返回详细类型错误信息
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class) // 拦截所有运行时异常
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI(); // 获取请求路径
        log.error("请求地址'{}',发生未知异常.", requestURI, e); // 记录错误日志
        return AjaxResult.error(e.getMessage()); // 返回异常消息
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class) // 拦截所有未被捕获的异常
    public AjaxResult handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI(); // 获取请求路径
        log.error("请求地址'{}',发生系统异常.", requestURI, e); // 记录错误日志
        return AjaxResult.error(e.getMessage()); // 返回异常消息
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class) // 拦截参数绑定异常
    public AjaxResult handleBindException(BindException e)
    {
        log.error(e.getMessage(), e); // 记录错误日志
        String message = e.getAllErrors().get(0).getDefaultMessage(); // 获取第一个验证错误消息
        return AjaxResult.error(message); // 返回验证错误信息
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) // 拦截@Valid校验失败异常
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e); // 记录错误日志
        String message = e.getBindingResult().getFieldError().getDefaultMessage(); // 获取字段校验错误消息
        return AjaxResult.error(message); // 返回验证错误信息
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class) // 拦截演示模式操作异常
    public AjaxResult handleDemoModeException(DemoModeException e)
    {
        return AjaxResult.error("演示模式，不允许操作"); // 返回演示模式提示
    }
}
