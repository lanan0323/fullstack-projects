// 包声明
package com.debuff.quartz.util;

// 导入所需类
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.spring.SpringUtils;
import com.debuff.quartz.domain.SysJob;

/**
 * 任务执行工具
 *
 * @author ruoyi
 */
public class JobInvokeUtil // 任务执行工具类，通过反射调用指定的Bean方法
{
    /**
     * 执行方法
     *
     * @param sysJob 系统任务
     */
    public static void invokeMethod(SysJob sysJob) throws Exception // 根据任务信息反射调用目标方法
    {
        String invokeTarget = sysJob.getInvokeTarget(); // 获取调用目标字符串（如：beanName.methodName(params)）
        String beanName = getBeanName(invokeTarget); // 解析Bean名称
        String methodName = getMethodName(invokeTarget); // 解析方法名称
        List<Object[]> methodParams = getMethodParams(invokeTarget); // 解析方法参数

        if (!isValidClassName(beanName)) // 不是完整类名（即Spring Bean名称）
        {
            Object bean = SpringUtils.getBean(beanName); // 从Spring容器获取Bean
            invokeMethod(bean, methodName, methodParams); // 调用方法
        }
        else // 是完整类名
        {
            Object bean = Class.forName(beanName).getDeclaredConstructor().newInstance(); // 通过反射创建实例
            invokeMethod(bean, methodName, methodParams); // 调用方法
        }
    }

    /**
     * 调用任务方法
     *
     * @param bean 目标对象
     * @param methodName 方法名称
     * @param methodParams 方法参数
     */
    private static void invokeMethod(Object bean, String methodName, List<Object[]> methodParams) // 反射调用指定方法
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException
    {
        if (StringUtils.isNotNull(methodParams) && methodParams.size() > 0) // 有参数
        {
            Method method = bean.getClass().getMethod(methodName, getMethodParamsType(methodParams)); // 获取带参数的方法
            method.invoke(bean, getMethodParamsValue(methodParams)); // 反射调用方法
        }
        else // 无参数
        {
            Method method = bean.getClass().getMethod(methodName); // 获取无参方法
            method.invoke(bean); // 反射调用方法
        }
    }

    /**
     * 校验是否为为class包名
     * 
     * @param invokeTarget 名称
     * @return true是 false否
     */
    public static boolean isValidClassName(String invokeTarget) // 判断是否为完整类名（含多个.）
    {
        return StringUtils.countMatches(invokeTarget, ".") > 1; // 点数大于1说明是完整类名
    }

    /**
     * 获取bean名称
     * 
     * @param invokeTarget 目标字符串
     * @return bean名称
     */
    public static String getBeanName(String invokeTarget) // 从调用目标中解析Bean名称
    {
        String beanName = StringUtils.substringBefore(invokeTarget, "("); // 截取括号前的部分
        return StringUtils.substringBeforeLast(beanName, "."); // 取最后一个.之前的部分作为Bean名称
    }

    /**
     * 获取bean方法
     * 
     * @param invokeTarget 目标字符串
     * @return method方法
     */
    public static String getMethodName(String invokeTarget) // 从调用目标中解析方法名称
    {
        String methodName = StringUtils.substringBefore(invokeTarget, "("); // 截取括号前的部分
        return StringUtils.substringAfterLast(methodName, "."); // 取最后一个.之后的部分作为方法名
    }

    /**
     * 获取method方法参数相关列表
     * 
     * @param invokeTarget 目标字符串
     * @return method方法相关参数列表
     */
    public static List<Object[]> getMethodParams(String invokeTarget) // 解析调用目标中的参数列表
    {
        String methodStr = StringUtils.substringBetween(invokeTarget, "(", ")"); // 提取括号内的参数字符串
        if (StringUtils.isEmpty(methodStr)) // 无参数
        {
            return null;
        }
        String[] methodParams = methodStr.split(",(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)"); // 按逗号分割（考虑引号内的逗号）
        List<Object[]> classs = new LinkedList<>(); // 参数列表
        for (int i = 0; i < methodParams.length; i++) // 遍历每个参数
        {
            String str = StringUtils.trimToEmpty(methodParams[i]); // 去除空白
            // String字符串类型，以'或"开头
            if (StringUtils.startsWithAny(str, "'", "\"")) // 字符串类型参数
            {
                classs.add(new Object[] { StringUtils.substring(str, 1, str.length() - 1), String.class }); // 去掉引号
            }
            // boolean布尔类型，等于true或者false
            else if ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str)) // 布尔类型参数
            {
                classs.add(new Object[] { Boolean.valueOf(str), Boolean.class });
            }
            // long长整形，以L结尾
            else if (StringUtils.endsWith(str, "L")) // Long类型参数
            {
                classs.add(new Object[] { Long.valueOf(StringUtils.substring(str, 0, str.length() - 1)), Long.class });
            }
            // double浮点类型，以D结尾
            else if (StringUtils.endsWith(str, "D")) // Double类型参数
            {
                classs.add(new Object[] { Double.valueOf(StringUtils.substring(str, 0, str.length() - 1)), Double.class });
            }
            // 其他类型归类为整形
            else // 默认按Integer处理
            {
                classs.add(new Object[] { Integer.valueOf(str), Integer.class });
            }
        }
        return classs; // 返回参数列表（每个元素为[参数值, 参数类型]）
    }

    /**
     * 获取参数类型
     * 
     * @param methodParams 参数相关列表
     * @return 参数类型列表
     */
    public static Class<?>[] getMethodParamsType(List<Object[]> methodParams) // 从参数列表提取类型数组
    {
        Class<?>[] classs = new Class<?>[methodParams.size()]; // 创建类型数组
        int index = 0;
        for (Object[] os : methodParams) // 遍历参数列表
        {
            classs[index] = (Class<?>) os[1]; // 取每个参数的类型
            index++;
        }
        return classs;
    }

    /**
     * 获取参数值
     * 
     * @param methodParams 参数相关列表
     * @return 参数值列表
     */
    public static Object[] getMethodParamsValue(List<Object[]> methodParams) // 从参数列表提取值数组
    {
        Object[] classs = new Object[methodParams.size()]; // 创建值数组
        int index = 0;
        for (Object[] os : methodParams) // 遍历参数列表
        {
            classs[index] = (Object) os[0]; // 取每个参数的值
            index++;
        }
        return classs;
    }
}
