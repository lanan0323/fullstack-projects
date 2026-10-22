// 包声明
package com.debuff.framework.aspectj;

// 导入所需类
import java.util.Collection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson2.JSON;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.enums.BusinessStatus;
import com.debuff.common.enums.HttpMethod;
import com.debuff.common.filter.PropertyPreExcludeFilter;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.ServletUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.ip.IpUtils;
import com.debuff.framework.manager.AsyncManager;
import com.debuff.framework.manager.factory.AsyncFactory;
import com.debuff.system.domain.SysOperLog;

/**
 * 操作日志记录处理
 * 
 * @author ruoyi
 */
@Aspect // 声明为AOP切面
@Component // 注册为Spring Bean
public class LogAspect
{
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class); // 日志记录器

    /** 排除敏感属性字段 */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" }; // 日志中需要排除的密码相关敏感字段

    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time"); // 线程本地变量，记录请求开始时间用于计算耗时

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)") // 前置通知，在方法执行前记录开始时间
    public void boBefore(JoinPoint joinPoint, Log controllerLog)
    {
        TIME_THREADLOCAL.set(System.currentTimeMillis()); // 记录当前时间戳到线程本地变量
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult") // 返回通知，方法正常返回后记录日志
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object jsonResult)
    {
        handleLog(joinPoint, controllerLog, null, jsonResult); // 处理日志记录，无异常
    }

    /**
     * 拦截异常操作
     * 
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e") // 异常通知，方法抛异常后记录日志
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e)
    {
        handleLog(joinPoint, controllerLog, e, null); // 处理日志记录，携带异常信息
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object jsonResult) // 核心日志处理方法
    {
        try
        {
            // 获取当前的用户
            LoginUser loginUser = SecurityUtils.getLoginUser(); // 从Security上下文获取当前登录用户

            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog(); // 创建操作日志对象
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal()); // 默认设置操作状态为成功
            // 请求的地址
            String ip = IpUtils.getIpAddr(); // 获取客户端IP地址
            operLog.setOperIp(ip); // 设置操作IP
            operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255)); // 设置请求URL，截取前255字符
            if (loginUser != null) // 如果用户已登录
            {
                operLog.setOperName(loginUser.getUsername()); // 设置操作人用户名
                SysUser currentUser = loginUser.getUser(); // 获取用户详情
                if (StringUtils.isNotNull(currentUser) && StringUtils.isNotNull(currentUser.getDept())) // 如果用户部门信息不为空
                {
                    operLog.setDeptName(currentUser.getDept().getDeptName()); // 设置部门名称
                }
            }

            if (e != null) // 如果有异常
            {
                operLog.setStatus(BusinessStatus.FAIL.ordinal()); // 设置操作状态为失败
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000)); // 设置错误信息，截取前2000字符
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName(); // 获取目标类全限定名
            String methodName = joinPoint.getSignature().getName(); // 获取方法名
            operLog.setMethod(className + "." + methodName + "()"); // 拼接并设置方法名称
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod()); // 设置HTTP请求方式（GET/POST等）
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult); // 从注解中提取日志描述信息
            // 设置消耗时间
            operLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get()); // 计算并设置操作耗时（毫秒）
            // 保存数据库
            AsyncManager.me().execute(AsyncFactory.recordOper(operLog)); // 异步保存操作日志到数据库
        }
        catch (Exception exp) // 捕获日志处理过程中的异常
        {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage()); // 打印错误日志
            exp.printStackTrace(); // 打印异常堆栈
        }
        finally
        {
            TIME_THREADLOCAL.remove(); // 清除线程本地变量，防止内存泄漏
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     * 
     * @param log 日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog, Object jsonResult) throws Exception // 从@Log注解中提取描述信息填充到操作日志
    {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal()); // 设置业务类型（如新增、修改、删除等）
        // 设置标题
        operLog.setTitle(log.title()); // 设置操作标题
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal()); // 设置操作者类型（如后台用户、手机端用户）
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) // 如果注解配置了保存请求参数
        {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog, log.excludeParamNames()); // 提取并设置请求参数
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && StringUtils.isNotNull(jsonResult)) // 如果配置了保存响应且响应不为空
        {
            operLog.setJsonResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000)); // 将响应结果JSON化并截取前2000字符保存
        }
    }

    /**
     * 获取请求的参数，放到log中
     * 
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog, String[] excludeParamNames) throws Exception // 提取请求参数并设置到日志对象
    {
        Map<?, ?> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest()); // 获取请求中的参数Map
        String requestMethod = operLog.getRequestMethod(); // 获取请求方式
        if (StringUtils.isEmpty(paramsMap) // 如果参数Map为空
                && (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod))) // 且是PUT或POST请求
        {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames); // 从方法参数数组中提取参数
            operLog.setOperParam(StringUtils.substring(params, 0, 2000)); // 设置操作参数，截取前2000字符
        }
        else
        {
            operLog.setOperParam(StringUtils.substring(JSON.toJSONString(paramsMap, excludePropertyPreFilter(excludeParamNames)), 0, 2000)); // 将参数Map序列化为JSON并保存
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) // 将方法参数数组转换为字符串
    {
        String params = ""; // 初始化参数字符串
        if (paramsArray != null && paramsArray.length > 0) // 如果参数数组不为空
        {
            for (Object o : paramsArray) // 遍历每个参数
            {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) // 参数不为空且不是需要过滤的类型
                {
                    try
                    {
                        String jsonObj = JSON.toJSONString(o, excludePropertyPreFilter(excludeParamNames)); // 将参数序列化为JSON，排除敏感属性
                        params += jsonObj.toString() + " "; // 拼接到参数字符串
                    }
                    catch (Exception e) // 序列化失败时忽略该参数
                    {
                    }
                }
            }
        }
        return params.trim(); // 返回去除首尾空格的参数字符串
    }

    /**
     * 忽略敏感属性
     */
    public PropertyPreExcludeFilter excludePropertyPreFilter(String[] excludeParamNames) // 创建属性过滤器，排除敏感字段
    {
        return new PropertyPreExcludeFilter().addExcludes(ArrayUtils.addAll(EXCLUDE_PROPERTIES, excludeParamNames)); // 合并默认排除字段和注解指定的排除字段
    }

    /**
     * 判断是否需要过滤的对象。
     * 
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) // 判断参数对象是否为需要过滤的类型（如文件上传、请求响应对象等）
    {
        Class<?> clazz = o.getClass(); // 获取对象的Class
        if (clazz.isArray()) // 如果是数组类型
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class); // 判断数组元素是否为MultipartFile
        }
        else if (Collection.class.isAssignableFrom(clazz)) // 如果是集合类型
        {
            Collection collection = (Collection) o; // 转换为集合
            for (Object value : collection) // 遍历集合元素
            {
                return value instanceof MultipartFile; // 判断是否为MultipartFile类型
            }
        }
        else if (Map.class.isAssignableFrom(clazz)) // 如果是Map类型
        {
            Map map = (Map) o; // 转换为Map
            for (Object value : map.entrySet()) // 遍历Map条目
            {
                Map.Entry entry = (Map.Entry) value; // 获取Map条目
                return entry.getValue() instanceof MultipartFile; // 判断值是否为MultipartFile
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse // 判断是否为文件、请求、响应或绑定结果对象
                || o instanceof BindingResult;
    }
}
