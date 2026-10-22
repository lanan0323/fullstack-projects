// 包声明：UUID实现类所在包
package com.debuff.common.utils.uuid;

// 导入安全随机数、消息摘要、随机数、并发随机数、异常等
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import com.debuff.common.exception.UtilException;

/**
 * 提供通用唯一识别码（universally unique identifier）（UUID）实现
 *
 * @author ruoyi
 */
public final class UUID implements java.io.Serializable, Comparable<UUID> // UUID类，实现序列化和比较接口
{
    private static final long serialVersionUID = -1185015143654744140L; // 序列化版本号

    /**
     * SecureRandom 的单例
     *
     */
    private static class Holder // 静态内部类，延迟初始化SecureRandom
    {
        static final SecureRandom numberGenerator = getSecureRandom(); // 获取安全随机数生成器
    }

    /** 此UUID的最高64有效位 */
    private final long mostSigBits; // 高64位

    /** 此UUID的最低64有效位 */
    private final long leastSigBits; // 低64位

    /**
     * 私有构造
     * 
     * @param data 数据
     */
    private UUID(byte[] data) // 从16字节数组构造UUID
    {
        long msb = 0; // 高64位
        long lsb = 0; // 低64位
        assert data.length == 16 : "data must be 16 bytes in length"; // 断言数据长度为16
        for (int i = 0; i < 8; i++) // 前8字节组成高64位
        {
            msb = (msb << 8) | (data[i] & 0xff); // 逐字节拼接高64位
        }
        for (int i = 8; i < 16; i++) // 后8字节组成低64位
        {
            lsb = (lsb << 8) | (data[i] & 0xff); // 逐字节拼接低64位
        }
        this.mostSigBits = msb; // 赋值高64位
        this.leastSigBits = lsb; // 赋值低64位
    }

    /**
     * 使用指定的数据构造新的 UUID。
     *
     * @param mostSigBits 用于 {@code UUID} 的最高有效 64 位
     * @param leastSigBits 用于 {@code UUID} 的最低有效 64 位
     */
    public UUID(long mostSigBits, long leastSigBits) // 从高64位和低64位构造UUID
    {
        this.mostSigBits = mostSigBits; // 赋值高64位
        this.leastSigBits = leastSigBits; // 赋值低64位
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。
     * 
     * @return 随机生成的 {@code UUID}
     */
    public static UUID fastUUID() // 使用ThreadLocalRandom高性能生成UUID
    {
        return randomUUID(false); // 不使用SecureRandom
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的强伪随机数生成器生成该 UUID。
     * 
     * @return 随机生成的 {@code UUID}
     */
    public static UUID randomUUID() // 使用SecureRandom安全生成UUID
    {
        return randomUUID(true); // 使用SecureRandom
    }

    /**
     * 获取类型 4（伪随机生成的）UUID 的静态工厂。 使用加密的强伪随机数生成器生成该 UUID。
     * 
     * @param isSecure 是否使用{@link SecureRandom}如果是可以获得更安全的随机码，否则可以得到更好的性能
     * @return 随机生成的 {@code UUID}
     */
    public static UUID randomUUID(boolean isSecure) // 根据参数选择随机数生成器生成UUID
    {
        final Random ng = isSecure ? Holder.numberGenerator : getRandom(); // 选择随机数生成器

        byte[] randomBytes = new byte[16]; // 创建16字节随机数组
        ng.nextBytes(randomBytes); // 填充随机数据
        randomBytes[6] &= 0x0f; /* clear version */ // 清除版本号位
        randomBytes[6] |= 0x40; /* set to version 4 */ // 设置版本号为4
        randomBytes[8] &= 0x3f; /* clear variant */ // 清除变体位
        randomBytes[8] |= 0x80; /* set to IETF variant */ // 设置IETF变体
        return new UUID(randomBytes); // 返回新UUID
    }

    /**
     * 根据指定的字节数组获取类型 3（基于名称的）UUID 的静态工厂。
     *
     * @param name 用于构造 UUID 的字节数组。
     *
     * @return 根据指定数组生成的 {@code UUID}
     */
    public static UUID nameUUIDFromBytes(byte[] name) // 基于字节数组的MD5生成UUID（版本3）
    {
        MessageDigest md; // 消息摘要对象
        try
        {
            md = MessageDigest.getInstance("MD5"); // 获取MD5摘要
        }
        catch (NoSuchAlgorithmException nsae)
        {
            throw new InternalError("MD5 not supported"); // 不支持MD5时抛出错误
        }
        byte[] md5Bytes = md.digest(name); // 计算MD5摘要
        md5Bytes[6] &= 0x0f; /* clear version */ // 清除版本号位
        md5Bytes[6] |= 0x30; /* set to version 3 */ // 设置版本号为3
        md5Bytes[8] &= 0x3f; /* clear variant */ // 清除变体位
        md5Bytes[8] |= 0x80; /* set to IETF variant */ // 设置IETF变体
        return new UUID(md5Bytes); // 返回新UUID
    }

    /**
     * 根据 {@link #toString()} 方法中描述的字符串标准表示形式创建{@code UUID}。
     *
     * @param name 指定 {@code UUID} 字符串
     * @return 具有指定值的 {@code UUID}
     * @throws IllegalArgumentException 如果 name 与 {@link #toString} 中描述的字符串表示形式不符抛出此异常
     *
     */
    public static UUID fromString(String name) // 从标准UUID字符串解析UUID对象
    {
        String[] components = name.split("-"); // 按-分割为5部分
        if (components.length != 5) // 不是5部分时
        {
            throw new IllegalArgumentException("Invalid UUID string: " + name); // 抛出参数异常
        }
        for (int i = 0; i < 5; i++) // 给每部分加上0x前缀
        {
            components[i] = "0x" + components[i];
        }

        long mostSigBits = Long.decode(components[0]).longValue(); // 解析第1部分
        mostSigBits <<= 16; // 左移16位
        mostSigBits |= Long.decode(components[1]).longValue(); // 合并第2部分
        mostSigBits <<= 16; // 左移16位
        mostSigBits |= Long.decode(components[2]).longValue(); // 合并第3部分

        long leastSigBits = Long.decode(components[3]).longValue(); // 解析第4部分
        leastSigBits <<= 48; // 左移48位
        leastSigBits |= Long.decode(components[4]).longValue(); // 合并第5部分

        return new UUID(mostSigBits, leastSigBits); // 返回新UUID
    }

    /**
     * 返回此 UUID 的 128 位值中的最低有效 64 位。
     *
     * @return 此 UUID 的 128 位值中的最低有效 64 位。
     */
    public long getLeastSignificantBits() // 获取低64位
    {
        return leastSigBits; // 返回低64位
    }

    /**
     * 返回此 UUID 的 128 位值中的最高有效 64 位。
     *
     * @return 此 UUID 的 128 位值中最高有效 64 位。
     */
    public long getMostSignificantBits() // 获取高64位
    {
        return mostSigBits; // 返回高64位
    }

    /**
     * 与此 {@code UUID} 相关联的版本号. 版本号描述此 {@code UUID} 是如何生成的。
     *
     * @return 此 {@code UUID} 的版本号
     */
    public int version() // 获取UUID版本号
    {
        // Version is bits masked by 0x000000000000F000 in MS long
        return (int) ((mostSigBits >> 12) & 0x0f); // 提取版本号位
    }

    /**
     * 与此 {@code UUID} 相关联的变体号。变体号描述 {@code UUID} 的布局。
     *
     * @return 此 {@code UUID} 相关联的变体号
     */
    public int variant() // 获取UUID变体号
    {
        // This field is composed of a varying number of bits.
        // 0 - - Reserved for NCS backward compatibility
        // 1 0 - The IETF aka Leach-Salz variant (used by this class)
        // 1 1 0 Reserved, Microsoft backward compatibility
        // 1 1 1 Reserved for future definition.
        return (int) ((leastSigBits >>> (64 - (leastSigBits >>> 62))) & (leastSigBits >> 63)); // 提取变体位
    }

    /**
     * 与此 UUID 相关联的时间戳值。
     *
     * @throws UnsupportedOperationException 如果此 {@code UUID} 不是 version 为 1 的 UUID。
     */
    public long timestamp() throws UnsupportedOperationException // 获取时间戳（仅版本1有效）
    {
        checkTimeBase(); // 检查是否为时间-based版本
        return (mostSigBits & 0x0FFFL) << 48// 提取time_low
                | ((mostSigBits >> 16) & 0x0FFFFL) << 32// 提取time_mid
                | mostSigBits >>> 32; // 提取time_hi
    }

    /**
     * 与此 UUID 相关联的时钟序列值。
     *
     * @return 此 {@code UUID} 的时钟序列
     *
     * @throws UnsupportedOperationException 如果此 UUID 的 version 不为 1
     */
    public int clockSequence() throws UnsupportedOperationException // 获取时钟序列（仅版本1有效）
    {
        checkTimeBase(); // 检查是否为时间-based版本
        return (int) ((leastSigBits & 0x3FFF000000000000L) >>> 48); // 提取时钟序列位
    }

    /**
     * 与此 UUID 相关的节点值。
     *
     * @return 此 {@code UUID} 的节点值
     *
     * @throws UnsupportedOperationException 如果此 {@code UUID} 的 version 不为 1
     */
    public long node() throws UnsupportedOperationException // 获取节点值（仅版本1有效）
    {
        checkTimeBase(); // 检查是否为时间-based版本
        return leastSigBits & 0x0000FFFFFFFFFFFFL; // 提取节点位
    }

    /**
     * 返回此{@code UUID} 的字符串表现形式。
     *
     * @return 此{@code UUID} 的字符串表现形式
     * @see #toString(boolean)
     */
    @Override
    public String toString() // 默认带横线的字符串形式
    {
        return toString(false); // 调用带参数的toString
    }

    /**
     * 返回此{@code UUID} 的字符串表现形式。
     *
     * @param isSimple 是否简单模式，简单模式为不带'-'的UUID字符串
     * @return 此{@code UUID} 的字符串表现形式
     */
    public String toString(boolean isSimple) // 根据参数决定是否带横线
    {
        final StringBuilder builder = new StringBuilder(isSimple ? 32 : 36); // 创建StringBuilder
        // time_low
        builder.append(digits(mostSigBits >> 32, 8)); // 追加time_low部分
        if (!isSimple) // 非简单模式
        {
            builder.append('-'); // 追加横线
        }
        // time_mid
        builder.append(digits(mostSigBits >> 16, 4)); // 追加time_mid部分
        if (!isSimple) // 非简单模式
        {
            builder.append('-'); // 追加横线
        }
        // time_high_and_version
        builder.append(digits(mostSigBits, 4)); // 追加time_high_and_version部分
        if (!isSimple) // 非简单模式
        {
            builder.append('-'); // 追加横线
        }
        // variant_and_sequence
        builder.append(digits(leastSigBits >> 48, 4)); // 追加variant_and_sequence部分
        if (!isSimple) // 非简单模式
        {
            builder.append('-'); // 追加横线
        }
        // node
        builder.append(digits(leastSigBits, 12)); // 追加node部分

        return builder.toString(); // 返回完整UUID字符串
    }

    /**
     * 返回此 UUID 的哈希码。
     *
     * @return UUID 的哈希码值。
     */
    @Override
    public int hashCode() // 计算哈希码
    {
        long hilo = mostSigBits ^ leastSigBits; // 高低64位异或
        return ((int) (hilo >> 32)) ^ (int) hilo; // 高低32位异或
    }

    /**
     * 将此对象与指定对象比较。
     *
     * @param obj 要与之比较的对象
     *
     * @return 如果对象相同，则返回 {@code true}；否则返回 {@code false}
     */
    @Override
    public boolean equals(Object obj) // 比较两个UUID是否相等
    {
        if ((null == obj) || (obj.getClass() != UUID.class)) // 非UUID类型时
        {
            return false; // 返回false
        }
        UUID id = (UUID) obj; // 转为UUID
        return (mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits); // 比较高低64位
    }

    // Comparison Operations

    /**
     * 将此 UUID 与指定的 UUID 比较。
     *
     * @param val 与此 UUID 比较的 UUID
     *
     * @return 在此 UUID 小于、等于或大于 val 时，分别返回 -1、0 或 1。
     *
     */
    @Override
    public int compareTo(UUID val) // 比较两个UUID大小
    {
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        return (this.mostSigBits < val.mostSigBits ? -1 : // 先比较高64位
                (this.mostSigBits > val.mostSigBits ? 1 : // 高64位大则返回1
                        (this.leastSigBits < val.leastSigBits ? -1 : // 再比较低64位
                                (this.leastSigBits > val.leastSigBits ? 1 : // 低64位大则返回1
                                        0)))); // 相等返回0
    }

    // -------------------------------------------------------------------------------------------------------------------
    // Private method start
    /**
     * 返回指定数字对应的hex值
     * 
     * @param val 值
     * @param digits 位
     * @return 值
     */
    private static String digits(long val, int digits) // 将数字转为指定位数的十六进制字符串
    {
        long hi = 1L << (digits * 4); // 计算高位掩码
        return Long.toHexString(hi | (val & (hi - 1))).substring(1); // 转十六进制并截取
    }

    /**
     * 检查是否为time-based版本UUID
     */
    private void checkTimeBase() // 检查是否为基于时间的UUID（版本1）
    {
        if (version() != 1) // 版本不是1时
        {
            throw new UnsupportedOperationException("Not a time-based UUID"); // 抛出不支持异常
        }
    }

    /**
     * 获取{@link SecureRandom}，类提供加密的强随机数生成器 (RNG)
     * 
     * @return {@link SecureRandom}
     */
    public static SecureRandom getSecureRandom() // 获取SHA1PRNG安全随机数生成器
    {
        try
        {
            return SecureRandom.getInstance("SHA1PRNG"); // 获取SHA1PRNG算法实例
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new UtilException(e); // 包装为工具异常抛出
        }
    }

    /**
     * 获取随机数生成器对象<br>
     * ThreadLocalRandom是JDK 7之后提供并发产生随机数，能够解决多个线程发生的竞争争夺。
     * 
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom getRandom() // 获取ThreadLocalRandom高性能随机数生成器
    {
        return ThreadLocalRandom.current(); // 返回当前线程的随机数生成器
    }
}
