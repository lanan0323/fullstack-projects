// 包声明：序列生成类所在包
package com.debuff.common.utils.uuid;

// 导入原子整数、日期工具、字符串工具
import java.util.concurrent.atomic.AtomicInteger;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.StringUtils;

/**
 * @author ruoyi 序列生成类
 */
public class Seq // 序列号生成工具类，生成时间+机器标识+递增序号的唯一序列
{
    // 通用序列类型
    public static final String commSeqType = "COMMON"; // 通用序列类型标识

    // 上传序列类型
    public static final String uploadSeqType = "UPLOAD"; // 上传序列类型标识

    // 通用接口序列数
    private static AtomicInteger commSeq = new AtomicInteger(1); // 通用序列原子计数器，初始值1

    // 上传接口序列数
    private static AtomicInteger uploadSeq = new AtomicInteger(1); // 上传序列原子计数器，初始值1

    // 机器标识
    private static final String machineCode = "A"; // 机器标识字符，用于区分不同机器

    /**
     * 获取通用序列号
     * 
     * @return 序列值
     */
    public static String getId() // 获取通用类型的序列号
    {
        return getId(commSeqType); // 调用带类型参数的方法
    }
    
    /**
     * 默认16位序列号 yyMMddHHmmss + 一位机器标识 + 3长度循环递增字符串
     * 
     * @return 序列值
     */
    public static String getId(String type) // 根据类型获取序列号
    {
        AtomicInteger atomicInt = commSeq; // 默认使用通用序列计数器
        if (uploadSeqType.equals(type)) // 如果是上传类型
        {
            atomicInt = uploadSeq; // 使用上传序列计数器
        }
        return getId(atomicInt, 3); // 调用带长度参数的方法，序号长度3
    }

    /**
     * 通用接口序列号 yyMMddHHmmss + 一位机器标识 + length长度循环递增字符串
     * 
     * @param atomicInt 序列数
     * @param length 数值长度
     * @return 序列值
     */
    public static String getId(AtomicInteger atomicInt, int length) // 拼接时间+机器标识+递增序号
    {
        String result = DateUtils.dateTimeNow(); // 获取当前时间字符串yyMMddHHmmss
        result += machineCode; // 追加机器标识
        result += getSeq(atomicInt, length); // 追加递增序号
        return result; // 返回完整序列号
    }

    /**
     * 序列循环递增字符串[1, 10 的 (length)幂次方), 用0左补齐length位数
     * 
     * @return 序列值
     */
    private synchronized static String getSeq(AtomicInteger atomicInt, int length) // 获取递增序号字符串，循环递增
    {
        // 先取值再+1
        int value = atomicInt.getAndIncrement(); // 原子获取当前值并+1

        // 如果更新后值>=10 的 (length)幂次方则重置为1
        int maxSeq = (int) Math.pow(10, length); // 计算最大值，如length=3时为1000
        if (atomicInt.get() >= maxSeq) // 超过最大值时
        {
            atomicInt.set(1); // 重置为1
        }
        // 转字符串，用0左补齐
        return StringUtils.padl(value, length); // 左补0到指定长度
    }
}
