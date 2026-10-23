// 包声明
package com.debuff.common.core.text;

// 导入类型转换和字符集工具所需的类
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Set;
import com.debuff.common.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 类型转换器
 *
 * @author ruoyi
 */
public class Convert
{
    /**
     * 转换为字符串<br>
     * 如果给定的值为null，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static String toStr(Object value, String defaultValue)
    {
        if (null == value) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof String) // 已经是字符串直接返回
        {
            return (String) value;
        }
        return value.toString(); // 调用toString转换
    }

    /**
     * 转换为字符串<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static String toStr(Object value)
    {
        return toStr(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为字符<br>
     * 如果给定的值为null，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Character toChar(Object value, Character defaultValue)
    {
        if (null == value) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Character) // 已经是字符类型直接返回
        {
            return (Character) value;
        }

        final String valueStr = toStr(value, null); // 先转为字符串
        return StringUtils.isEmpty(valueStr) ? defaultValue : valueStr.charAt(0); // 取第一个字符
    }

    /**
     * 转换为字符<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Character toChar(Object value)
    {
        return toChar(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Byte toByte(Object value, Byte defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Byte) // 已经是Byte类型直接返回
        {
            return (Byte) value;
        }
        if (value instanceof Number) // 是数字类型直接转byte
        {
            return ((Number) value).byteValue();
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return Byte.parseByte(valueStr); // 解析字符串为byte
        }
        catch (Exception e) // 解析失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为byte<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Byte toByte(Object value)
    {
        return toByte(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为Short<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Short toShort(Object value, Short defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Short) // 已经是Short类型直接返回
        {
            return (Short) value;
        }
        if (value instanceof Number) // 是数字类型直接转short
        {
            return ((Number) value).shortValue();
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return Short.parseShort(valueStr.trim()); // 去除空白后解析为short
        }
        catch (Exception e) // 解析失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为Short<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Short toShort(Object value)
    {
        return toShort(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为Number<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Number toNumber(Object value, Number defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Number) // 已经是Number类型直接返回
        {
            return (Number) value;
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return NumberFormat.getInstance().parse(valueStr); // 使用NumberFormat解析数字
        }
        catch (Exception e) // 解析失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为Number<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Number toNumber(Object value)
    {
        return toNumber(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为int<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Integer toInt(Object value, Integer defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Integer) // 已经是Integer类型直接返回
        {
            return (Integer) value;
        }
        if (value instanceof Number) // 是数字类型直接转int
        {
            return ((Number) value).intValue();
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt(valueStr.trim()); // 去除空白后解析为int
        }
        catch (Exception e) // 解析失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为int<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Integer toInt(Object value)
    {
        return toInt(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为Integer数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static Integer[] toIntArray(String str)
    {
        return toIntArray(",", str); // 默认用逗号分隔
    }

    /**
     * 转换为Long数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static Long[] toLongArray(String str)
    {
        return toLongArray(",", str); // 默认用逗号分隔
    }

    /**
     * 转换为Integer数组<br>
     *
     * @param split 分隔符
     * @param split 被转换的值
     * @return 结果
     */
    public static Integer[] toIntArray(String split, String str)
    {
        if (StringUtils.isEmpty(str)) // 字符串为空返回空数组
        {
            return new Integer[] {};
        }
        String[] arr = str.split(split); // 按分隔符拆分字符串
        final Integer[] ints = new Integer[arr.length]; // 创建Integer数组
        for (int i = 0; i < arr.length; i++) // 遍历每个元素
        {
            final Integer v = toInt(arr[i], 0); // 逐个转换为int
            ints[i] = v;
        }
        return ints; // 返回转换后的数组
    }

    /**
     * 转换为Long数组<br>
     *
     * @param split 分隔符
     * @param str 被转换的值
     * @return 结果
     */
    public static Long[] toLongArray(String split, String str)
    {
        if (StringUtils.isEmpty(str)) // 字符串为空返回空数组
        {
            return new Long[] {};
        }
        String[] arr = str.split(split); // 按分隔符拆分字符串
        final Long[] longs = new Long[arr.length]; // 创建Long数组
        for (int i = 0; i < arr.length; i++) // 遍历每个元素
        {
            final Long v = toLong(arr[i], null); // 逐个转换为long
            longs[i] = v;
        }
        return longs; // 返回转换后的数组
    }

    /**
     * 转换为String数组<br>
     *
     * @param str 被转换的值
     * @return 结果
     */
    public static String[] toStrArray(String str)
    {
        return toStrArray(",", str); // 默认用逗号分隔
    }

    /**
     * 转换为String数组<br>
     *
     * @param split 分隔符
     * @param split 被转换的值
     * @return 结果
     */
    public static String[] toStrArray(String split, String str)
    {
        return str.split(split); // 按分隔符拆分为字符串数组
    }

    /**
     * 转换为long<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Long toLong(Object value, Long defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Long) // 已经是Long类型直接返回
        {
            return (Long) value;
        }
        if (value instanceof Number) // 是数字类型直接转long
        {
            return ((Number) value).longValue();
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            // 支持科学计数法
            return new BigDecimal(valueStr.trim()).longValue(); // 用BigDecimal解析支持科学计数法
        }
        catch (Exception e) // 解析失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为long<br>
     * 如果给定的值为<code>null</code>，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Long toLong(Object value)
    {
        return toLong(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为double<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Double toDouble(Object value, Double defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Double) // 已经是Double类型直接返回
        {
            return (Double) value;
        }
        if (value instanceof Number) // 是数字类型直接转double
        {
            return ((Number) value).doubleValue();
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            // 支持科学计数法
            return new BigDecimal(valueStr.trim()).doubleValue(); // 用BigDecimal解析支持科学计数法
        }
        catch (Exception e) // 解析失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为double<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Double toDouble(Object value)
    {
        return toDouble(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为Float<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Float toFloat(Object value, Float defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Float) // 已经是Float类型直接返回
        {
            return (Float) value;
        }
        if (value instanceof Number) // 是数字类型直接转float
        {
            return ((Number) value).floatValue();
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return Float.parseFloat(valueStr.trim()); // 去除空白后解析为float
        }
        catch (Exception e) // 解析失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为Float<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Float toFloat(Object value)
    {
        return toFloat(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为boolean<br>
     * String支持的值为：true、false、yes、ok、no，1,0 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Boolean toBool(Object value, Boolean defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof Boolean) // 已经是Boolean类型直接返回
        {
            return (Boolean) value;
        }
        String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        valueStr = valueStr.trim().toLowerCase(); // 去除空白并转小写
        switch (valueStr) // 根据字符串值判断布尔值
        {
            case "true": // true/yes/ok/1返回true
            case "yes":
            case "ok":
            case "1":
                return true;
            case "false": // false/no/0返回false
            case "no":
            case "0":
                return false;
            default: // 其他值返回默认值
                return defaultValue;
        }
    }

    /**
     * 转换为boolean<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static Boolean toBool(Object value)
    {
        return toBool(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为Enum对象<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     *
     * @param clazz Enum的Class
     * @param value 值
     * @param defaultValue 默认值
     * @return Enum
     */
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (clazz.isAssignableFrom(value.getClass())) // 值已经是目标枚举类型
        {
            @SuppressWarnings("unchecked")
            E myE = (E) value; // 直接强转返回
            return myE;
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return Enum.valueOf(clazz, valueStr); // 根据名称获取枚举常量
        }
        catch (Exception e) // 转换失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为Enum对象<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     *
     * @param clazz Enum的Class
     * @param value 值
     * @return Enum
     */
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value)
    {
        return toEnum(clazz, value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为BigInteger<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static BigInteger toBigInteger(Object value, BigInteger defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof BigInteger) // 已经是BigInteger类型直接返回
        {
            return (BigInteger) value;
        }
        if (value instanceof Long) // 是Long类型通过valueOf转换
        {
            return BigInteger.valueOf((Long) value);
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return new BigInteger(valueStr); // 用字符串构造BigInteger
        }
        catch (Exception e) // 转换失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为BigInteger<br>
     * 如果给定的值为空，或者转换失败，返回默认值<code>null</code><br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static BigInteger toBigInteger(Object value)
    {
        return toBigInteger(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue)
    {
        if (value == null) // 值为null时返回默认值
        {
            return defaultValue;
        }
        if (value instanceof BigDecimal) // 已经是BigDecimal类型直接返回
        {
            return (BigDecimal) value;
        }
        if (value instanceof Long) // 是Long类型构造BigDecimal
        {
            return new BigDecimal((Long) value);
        }
        if (value instanceof Double) // 是Double类型通过valueOf构造
        {
            return BigDecimal.valueOf((Double) value);
        }
        if (value instanceof Integer) // 是Integer类型构造BigDecimal
        {
            return new BigDecimal((Integer) value);
        }
        final String valueStr = toStr(value, null); // 先转为字符串
        if (StringUtils.isEmpty(valueStr)) // 字符串为空返回默认值
        {
            return defaultValue;
        }
        try
        {
            return new BigDecimal(valueStr); // 用字符串构造BigDecimal
        }
        catch (Exception e) // 转换失败返回默认值
        {
            return defaultValue;
        }
    }

    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value 被转换的值
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object value)
    {
        return toBigDecimal(value, null); // 委托给带默认值的重载方法
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj)
    {
        return str(obj, CharsetKit.CHARSET_UTF_8); // 使用UTF-8字符集转换
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @param charsetName 字符集
     * @return 字符串
     */
    public static String str(Object obj, String charsetName)
    {
        return str(obj, Charset.forName(charsetName)); // 将字符集名称转为Charset对象
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset)
    {
        if (null == obj) // 对象为null返回null
        {
            return null;
        }

        if (obj instanceof String) // 已经是字符串直接返回
        {
            return (String) obj;
        }
        else if (obj instanceof byte[]) // byte数组按指定字符集转字符串
        {
            return str((byte[]) obj, charset);
        }
        else if (obj instanceof Byte[]) // Byte包装数组先转基本类型再转字符串
        {
            byte[] bytes = ArrayUtils.toPrimitive((Byte[]) obj);
            return str(bytes, charset);
        }
        else if (obj instanceof ByteBuffer) // ByteBuffer解码为字符串
        {
            return str((ByteBuffer) obj, charset);
        }
        return obj.toString(); // 其他类型调用toString
    }

    /**
     * 将byte数组转为字符串
     *
     * @param bytes byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(byte[] bytes, String charset)
    {
        return str(bytes, StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset)); // 空字符集用系统默认
    }

    /**
     * 解码字节码
     *
     * @param data 字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(byte[] data, Charset charset)
    {
        if (data == null) // 数据为null返回null
        {
            return null;
        }

        if (null == charset) // 字符集为null用平台默认编码
        {
            return new String(data);
        }
        return new String(data, charset); // 按指定字符集解码
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data 数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, String charset)
    {
        if (data == null) // 数据为null返回null
        {
            return null;
        }

        return str(data, Charset.forName(charset)); // 转为Charset对象后解码
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data 数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, Charset charset)
    {
        if (null == charset) // 字符集为null用系统默认
        {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString(); // 解码ByteBuffer为字符串
    }

    // ----------------------------------------------------------------------- 全角半角转换
    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input)
    {
        return toSBC(input, null); // 不跳过任何字符
    }

    /**
     * 半角转全角
     *
     * @param input String
     * @param notConvertSet 不替换的字符集合
     * @return 全角字符串.
     */
    public static String toSBC(String input, Set<Character> notConvertSet)
    {
        char[] c = input.toCharArray(); // 转为字符数组
        for (int i = 0; i < c.length; i++) // 遍历每个字符
        {
            if (null != notConvertSet && notConvertSet.contains(c[i]))
            {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == ' ') // 空格转全角空格
            {
                c[i] = '\u3000';
            }
            else if (c[i] < '\177') // ASCII字符加偏移量转全角
            {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c); // 返回全角字符串
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input)
    {
        return toDBC(input, null); // 不跳过任何字符
    }

    /**
     * 替换全角为半角
     *
     * @param text 文本
     * @param notConvertSet 不替换的字符集合
     * @return 替换后的字符
     */
    public static String toDBC(String text, Set<Character> notConvertSet)
    {
        char[] c = text.toCharArray(); // 转为字符数组
        for (int i = 0; i < c.length; i++) // 遍历每个字符
        {
            if (null != notConvertSet && notConvertSet.contains(c[i]))
            {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == '\u3000') // 全角空格转半角空格
            {
                c[i] = ' ';
            }
            else if (c[i] > '\uFF00' && c[i] < '\uFF5F') // 全角字符减偏移量转半角
            {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c); // 构造结果字符串

        return returnString; // 返回半角字符串
    }

    /**
     * 数字金额大写转换 先写个完整的然后将如零拾替换成零
     *
     * @param n 数字
     * @return 中文大写数字
     */
    public static String digitUppercase(double n)
    {
        String[] fraction = { "角", "分" }; // 小数位单位
        String[] digit = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" }; // 中文数字
        String[][] unit = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } }; // 整数位单位

        String head = n < 0 ? "负" : ""; // 判断正负号
        n = Math.abs(n); // 取绝对值

        String s = ""; // 小数部分结果
        for (int i = 0; i < fraction.length; i++) // 处理小数位
        {
            // 优化double计算精度丢失问题
            BigDecimal nNum = new BigDecimal(n); // 用BigDecimal避免精度丢失
            BigDecimal decimal = new BigDecimal(10);
            BigDecimal scale = nNum.multiply(decimal).setScale(2, RoundingMode.HALF_EVEN); // 保留两位小数
            double d = scale.doubleValue();
            s += (digit[(int) (Math.floor(d * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", ""); // 拼接小数部分
        }
        if (s.length() < 1) // 没有小数部分
        {
            s = "整";
        }
        int integerPart = (int) Math.floor(n); // 取整数部分

        for (int i = 0; i < unit[0].length && integerPart > 0; i++) // 处理整数部分(元/万/亿)
        {
            String p = "";
            for (int j = 0; j < unit[1].length && n > 0; j++) // 处理每级的个十百千
            {
                p = digit[integerPart % 10] + unit[1][j] + p; // 取末位数字拼接单位
                integerPart = integerPart / 10; // 去掉已处理的末位
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s; // 去除多余零后拼接
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整"); // 最终清理返回
    }
}
