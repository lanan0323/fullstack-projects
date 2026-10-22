// 包声明
package com.debuff.common.utils.reflect;

// 导入反射、日期、POI日期、日志、类型转换等类
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.debuff.common.core.text.Convert;
import com.debuff.common.utils.DateUtils;

// 反射工具类，提供调用getter/setter、访问私有变量、获取泛型类型等功能
@SuppressWarnings("rawtypes")
public class ReflectUtils
{
    // setter方法名前缀
    private static final String SETTER_PREFIX = "set";

    // getter方法名前缀
    private static final String GETTER_PREFIX = "get";

    // CGLIB代理类的分隔符
    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    // 调用Getter方法，支持多级（如 obj.name）
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName)
    {
        Object object = obj;
        // 按.分割属性名，逐级调用getter
        for (String name : StringUtils.split(propertyName, "."))
        {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name); // 拼接getter方法名
            object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {}); // 调用getter
        }
        return (E) object; // 返回最终值
    }

    // 调用Setter方法，支持多级（如 obj.name）
    public static <E> void invokeSetter(Object obj, String propertyName, E value)
    {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, "."); // 按.分割属性名
        for (int i = 0; i < names.length; i++)
        {
            if (i < names.length - 1) // 中间层级调用getter获取下一级对象
            {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
            }
            else // 最后一级调用setter设置值
            {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[] { value });
            }
        }
    }

    // 直接读取对象属性值，无视访问修饰符，不经过getter
    @SuppressWarnings("unchecked")
    public static <E> E getFieldValue(final Object obj, final String fieldName)
    {
        Field field = getAccessibleField(obj, fieldName); // 获取可访问的字段
        if (field == null) // 字段不存在
        {
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return null;
        }
        E result = null;
        try
        {
            result = (E) field.get(obj); // 反射获取字段值
        }
        catch (IllegalAccessException e)
        {
            logger.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    // 直接设置对象属性值，无视访问修饰符，不经过setter
    public static <E> void setFieldValue(final Object obj, final String fieldName, final E value)
    {
        Field field = getAccessibleField(obj, fieldName); // 获取可访问的字段
        if (field == null) // 字段不存在
        {
            // throw new IllegalArgumentException("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + fieldName + "] 字段 ");
            return;
        }
        try
        {
            field.set(obj, value); // 反射设置字段值
        }
        catch (IllegalAccessException e)
        {
            logger.error("不可能抛出的异常: {}", e.getMessage());
        }
    }

    // 直接调用对象方法，无视访问修饰符，匹配方法名+参数类型
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
            final Object[] args)
    {
        if (obj == null || methodName == null) // 参数为空
        {
            return null;
        }
        Method method = getAccessibleMethod(obj, methodName, parameterTypes); // 获取可访问的方法
        if (method == null) // 方法不存在
        {
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        }
        try
        {
            return (E) method.invoke(obj, args); // 反射调用方法
        }
        catch (Exception e)
        {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(msg, e); // 转为非受检异常
        }
    }

    // 直接调用对象方法（按名称匹配），无视访问修饰符，自动转换参数类型
    @SuppressWarnings("unchecked")
    public static <E> E invokeMethodByName(final Object obj, final String methodName, final Object[] args)
    {
        Method method = getAccessibleMethodByName(obj, methodName, args.length); // 按名称获取方法
        if (method == null) // 方法不存在
        {
            // 如果为空不报错，直接返回空。
            logger.debug("在 [" + obj.getClass() + "] 中，没有找到 [" + methodName + "] 方法 ");
            return null;
        }
        try
        {
            // 类型转换（将参数数据类型转换为目标方法参数类型）
            Class<?>[] cs = method.getParameterTypes(); // 获取方法参数类型
            for (int i = 0; i < cs.length; i++)
            {
                if (args[i] != null && !args[i].getClass().equals(cs[i])) // 参数类型不匹配时转换
                {
                    if (cs[i] == String.class) // 转String
                    {
                        args[i] = Convert.toStr(args[i]);
                        if (StringUtils.endsWith((String) args[i], ".0")) // 去掉.0后缀
                        {
                            args[i] = StringUtils.substringBefore((String) args[i], ".0");
                        }
                    }
                    else if (cs[i] == Integer.class) // 转Integer
                    {
                        args[i] = Convert.toInt(args[i]);
                    }
                    else if (cs[i] == Long.class) // 转Long
                    {
                        args[i] = Convert.toLong(args[i]);
                    }
                    else if (cs[i] == Double.class) // 转Double
                    {
                        args[i] = Convert.toDouble(args[i]);
                    }
                    else if (cs[i] == Float.class) // 转Float
                    {
                        args[i] = Convert.toFloat(args[i]);
                    }
                    else if (cs[i] == Date.class) // 转Date
                    {
                        if (args[i] instanceof String) // 字符串转日期
                        {
                            args[i] = DateUtils.parseDate(args[i]);
                        }
                        else // 数字转日期（Excel日期格式）
                        {
                            args[i] = DateUtil.getJavaDate((Double) args[i]);
                        }
                    }
                    else if (cs[i] == boolean.class || cs[i] == Boolean.class) // 转Boolean
                    {
                        args[i] = Convert.toBool(args[i]);
                    }
                }
            }
            return (E) method.invoke(obj, args); // 反射调用方法
        }
        catch (Exception e)
        {
            String msg = "method: " + method + ", obj: " + obj + ", args: " + args + "";
            throw convertReflectionExceptionToUnchecked(msg, e); // 转为非受检异常
        }
    }

    // 循环向上转型获取对象的声明字段，并设置为可访问
    public static Field getAccessibleField(final Object obj, final String fieldName)
    {
        // 为空不报错。直接返回 null
        if (obj == null)
        {
            return null;
        }
        Validate.notBlank(fieldName, "fieldName can't be blank"); // 校验字段名非空
        // 循环向上遍历父类
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass())
        {
            try
            {
                Field field = superClass.getDeclaredField(fieldName); // 获取声明字段
                makeAccessible(field); // 设置为可访问
                return field;
            }
            catch (NoSuchFieldException e) // 当前类没有该字段，继续向上查找
            {
                continue;
            }
        }
        return null; // 未找到返回null
    }

    // 循环向上转型获取声明方法（匹配方法名+参数类型），并设置为可访问
    public static Method getAccessibleMethod(final Object obj, final String methodName,
            final Class<?>... parameterTypes)
    {
        // 为空不报错。直接返回 null
        if (obj == null)
        {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank"); // 校验方法名非空
        // 循环向上遍历父类
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass())
        {
            try
            {
                Method method = searchType.getDeclaredMethod(methodName, parameterTypes); // 获取声明方法
                makeAccessible(method); // 设置为可访问
                return method;
            }
            catch (NoSuchMethodException e) // 当前类没有该方法，继续向上查找
            {
                continue;
            }
        }
        return null; // 未找到返回null
    }

    // 循环向上转型获取声明方法（只匹配方法名），并设置为可访问
    public static Method getAccessibleMethodByName(final Object obj, final String methodName, int argsNum)
    {
        // 为空不报错。直接返回 null
        if (obj == null)
        {
            return null;
        }
        Validate.notBlank(methodName, "methodName can't be blank"); // 校验方法名非空
        // 循环向上遍历父类
        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass())
        {
            Method[] methods = searchType.getDeclaredMethods(); // 获取所有声明方法
            for (Method method : methods)
            {
                // 方法名匹配且参数个数相同
                if (method.getName().equals(methodName) && method.getParameterTypes().length == argsNum)
                {
                    makeAccessible(method); // 设置为可访问
                    return method;
                }
            }
        }
        return null; // 未找到返回null
    }

    // 设置方法为可访问（处理private/protected修饰符）
    public static void makeAccessible(Method method)
    {
        // 方法或类不是public，且方法未设置可访问
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible())
        {
            method.setAccessible(true); // 设置为可访问
        }
    }

    // 设置字段为可访问（处理private/protected/final修饰符）
    public static void makeAccessible(Field field)
    {
        // 字段或类不是public，或字段是final，且未设置可访问
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers())) && !field.isAccessible())
        {
            field.setAccessible(true); // 设置为可访问
        }
    }

    // 获取类声明的泛型参数类型（默认第一个泛型参数）
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassGenricType(final Class clazz)
    {
        return getClassGenricType(clazz, 0);
    }

    // 获取类声明的父类的泛型参数类型（按索引）
    public static Class getClassGenricType(final Class clazz, final int index)
    {
        Type genType = clazz.getGenericSuperclass(); // 获取泛型父类类型

        if (!(genType instanceof ParameterizedType)) // 不是参数化类型
        {
            logger.debug(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments(); // 获取泛型参数数组

        if (index >= params.length || index < 0) // 索引越界
        {
            logger.debug("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) // 泛型参数不是Class类型
        {
            logger.debug(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index]; // 返回泛型参数的Class
    }

    // 获取用户的真实Class（如果是CGLIB代理则返回父类）
    public static Class<?> getUserClass(Object instance)
    {
        if (instance == null)
        {
            throw new RuntimeException("Instance must not be null");
        }
        Class clazz = instance.getClass(); // 获取实例的Class
        // 如果是CGLIB代理类（类名包含$$）
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR))
        {
            Class<?> superClass = clazz.getSuperclass(); // 获取父类（真实类）
            if (superClass != null && !Object.class.equals(superClass))
            {
                return superClass; // 返回真实类
            }
        }
        return clazz; // 非代理类直接返回
    }

    // 将反射的受检异常转换为非受检异常
    public static RuntimeException convertReflectionExceptionToUnchecked(String msg, Exception e)
    {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException)
        {
            return new IllegalArgumentException(msg, e); // 转为IllegalArgument异常
        }
        else if (e instanceof InvocationTargetException)
        {
            // 获取目标方法抛出的实际异常
            return new RuntimeException(msg, ((InvocationTargetException) e).getTargetException());
        }
        return new RuntimeException(msg, e); // 其他转为Runtime异常
    }
}