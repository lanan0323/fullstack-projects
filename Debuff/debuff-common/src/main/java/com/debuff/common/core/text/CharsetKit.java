// 包声明：文本处理工具所在包
package com.debuff.common.core.text;

// 导入Java字符集类
import java.nio.charset.Charset;
import  java.nio.charset.StandardCharsets;
// 导入字符串工具类
import com.debuff.common.utils.StringUtils;

/**
 * 字符集工具类
 * 
 * @author ruoyi
 */
public class CharsetKit // 字符集编码工具类
{
    /** ISO-8859-1 */
    public static final String ISO_8859_1 = "ISO-8859-1"; // ISO-8859-1编码名称

    /** UTF-8 */
    public static final String UTF_8 = "UTF-8"; // UTF-8编码名称

    /** GBK */
    public static final String GBK = "GBK"; // GBK编码名称

    /** ISO-8859-1 */
    public static final Charset CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1); // ISO-8859-1字符集对象

    /** UTF-8 */
    public static final Charset CHARSET_UTF_8 = Charset.forName(UTF_8); // UTF-8字符集对象

    /** GBK */
    public static final Charset CHARSET_GBK = Charset.forName(GBK); // GBK字符集对象

    /**
     * 转换为Charset对象
     */
    public static Charset charset(String charset) // 将字符串转为Charset对象
    {
        return StringUtils.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset); // 为空返回默认字符集
    }

    /**
     * 转换字符串的字符集编码
     */
    public static String convert(String source, String srcCharset, String destCharset) // 按名称指定源和目标编码进行转换
    {
        return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset)); // 调用Charset版本
    }

    /**
     * 转换字符串的字符集编码
     */
    public static String convert(String source, Charset srcCharset, Charset destCharset) // 按Charset对象进行编码转换
    {
        if (null == srcCharset) // 源编码为空时使用ISO-8859-1
        {
            srcCharset = StandardCharsets.ISO_8859_1;
        }

        if (null == destCharset) // 目标编码为空时使用UTF-8
        {
            destCharset = StandardCharsets.UTF_8;
        }

        if (StringUtils.isEmpty(source) || srcCharset.equals(destCharset)) // 源为空或编码相同时无需转换
        {
            return source; // 直接返回原字符串
        }
        return new String(source.getBytes(srcCharset), destCharset); // 先按源编码取字节，再按目标编码构造字符串
    }

    /**
     * @return 系统字符集编码
     */
    public static String systemCharset() // 获取系统默认字符集名称
    {
        return Charset.defaultCharset().name(); // 返回默认字符集名称
    }
}
