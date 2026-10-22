// 包声明：Bean工具类所在包
package com.debuff.common.utils.bean;

// 导入反射、集合、正则相关类
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 工具类
 * 
 * @author ruoyi
 */
public class BeanUtils extends org.springframework.beans.BeanUtils // 继承Spring的BeanUtils，扩展属性复制和getter/setter查找
{
    /** Bean方法名中属性名开始的下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3; // getter/setter方法名去掉get/set后第3个字符开始是属性名

    /** * 匹配getter方法的正则表达式 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)"); // 匹配getXxx格式

    /** * 匹配setter方法的正则表达式 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)"); // 匹配setXxx格式

    /**
     * Bean属性复制工具方法。
     * 
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src) // 将src对象的属性值复制到dest对象
    {
        try
        {
            copyProperties(src, dest); // 调用Spring的属性复制方法
        }
        catch (Exception e)
        {
            e.printStackTrace(); // 打印异常堆栈
        }
    }

    /**
     * 获取对象的setter方法。
     * 
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj) // 获取对象所有setter方法
    {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>(); // 创建setter方法列表

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods(); // 获取对象类的所有public方法

        // 查找setter方法

        for (Method method : methods) // 遍历所有方法
        {
            Matcher m = SET_PATTERN.matcher(method.getName()); // 用正则匹配是否为setter方法
            if (m.matches() && (method.getParameterTypes().length == 1)) // 匹配setXxx且只有一个参数
            {
                setterMethods.add(method); // 添加到setter方法列表
            }
        }
        // 返回setter方法列表
        return setterMethods; // 返回所有匹配的setter方法
    }

    /**
     * 获取对象的getter方法。
     * 
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj) // 获取对象所有getter方法
    {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>(); // 创建getter方法列表
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods(); // 获取对象类的所有public方法
        // 查找getter方法
        for (Method method : methods) // 遍历所有方法
        {
            Matcher m = GET_PATTERN.matcher(method.getName()); // 用正则匹配是否为getter方法
            if (m.matches() && (method.getParameterTypes().length == 0)) // 匹配getXxx且无参数
            {
                getterMethods.add(method); // 添加到getter方法列表
            }
        }
        // 返回getter方法列表
        return getterMethods; // 返回所有匹配的getter方法
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     * 
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2) // 比较两个方法名中的属性名是否相同
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX)); // 截取第3个字符后的属性名进行比较
    }
}
