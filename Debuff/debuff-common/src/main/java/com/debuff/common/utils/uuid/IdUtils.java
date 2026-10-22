// 包声明：ID生成器工具类所在包
package com.debuff.common.utils.uuid;

/**
 * ID生成器工具类
 * 
 * @author ruoyi
 */
public class IdUtils // ID生成器工具类，提供多种UUID生成方式
{
    /**
     * 获取随机UUID
     * 
     * @return 随机UUID
     */
    public static String randomUUID() // 生成标准格式的UUID（带横线）
    {
        return UUID.randomUUID().toString(); // 调用UUID类生成并转为字符串
    }

    /**
     * 简化的UUID，去掉了横线
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() // 生成简化UUID（不带横线）
    {
        return UUID.randomUUID().toString(true); // 调用UUID类生成，true表示简化模式
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 随机UUID
     */
    public static String fastUUID() // 使用高性能方式生成UUID（带横线）
    {
        return UUID.fastUUID().toString(); // 调用fastUUID方法生成
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() // 使用高性能方式生成简化UUID（不带横线）
    {
        return UUID.fastUUID().toString(true); // 调用fastUUID方法，true表示简化模式
    }
}
