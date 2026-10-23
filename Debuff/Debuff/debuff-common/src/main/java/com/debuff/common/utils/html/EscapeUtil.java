// 包声明：转义工具类所在包
package com.debuff.common.utils.html;

// 导入字符串工具类
import com.debuff.common.utils.StringUtils;

/**
 * 转义和反转义工具类
 * 
 * @author ruoyi
 */
public class EscapeUtil // HTML转义和反转义工具类
{
    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)"; // 匹配HTML标签的正则

    private static final char[][] TEXT = new char[][]{new char[64]}; // 特殊字符转义映射表

    static // 静态初始化块，填充转义映射表
    {
        for (int i = 0; i < 64; i++) // 默认每个字符映射为自身
        {
            TEXT[i] = new char[] { (char) i };
        }

        // special HTML characters
        TEXT['\''] = "&#039;".toCharArray(); // 单引号 // 单引号转义
        TEXT['"'] = "&#34;".toCharArray(); // 双引号 // 双引号转义
        TEXT['&'] = "&#38;".toCharArray(); // &符 // &转义
        TEXT['<'] = "&#60;".toCharArray(); // 小于号 // <转义
        TEXT['>'] = "&#62;".toCharArray(); // 大于号 // >转义
    }

    /**
     * 转义文本中的HTML字符为安全的字符
     * 
     * @param text 被转义的文本
     * @return 转义后的文本
     */
    public static String escape(String text) // 转义HTML特殊字符
    {
        return encode(text); // 调用编码方法
    }

    /**
     * 还原被转义的HTML特殊字符
     * 
     * @param content 包含转义符的HTML内容
     * @return 转换后的字符串
     */
    public static String unescape(String content) // 反转义HTML实体
    {
        return decode(content); // 调用解码方法
    }

    /**
     * 清除所有HTML标签，但是不删除标签内的内容
     * 
     * @param content 文本
     * @return 清除标签后的文本
     */
    public static String clean(String content) // 清除HTML标签保留内容
    {
        return new HTMLFilter().filter(content); // 使用HTMLFilter过滤
    }

    /**
     * Escape编码
     * 
     * @param text 被编码的文本
     * @return 编码后的字符
     */
    private static String encode(String text) // 对文本进行Escape百分号编码
    {
        if (StringUtils.isEmpty(text)) // 文本为空时
        {
            return StringUtils.EMPTY; // 返回空字符串
        }

        final StringBuilder tmp = new StringBuilder(text.length() * 6); // 创建缓冲区
        char c; // 当前字符
        for (int i = 0; i < text.length(); i++) // 遍历每个字符
        {
            c = text.charAt(i); // 获取当前字符
            if (c < 256) // ASCII字符
            {
                tmp.append("%"); // 追加%
                if (c < 16) // 小于16时补0
                {
                    tmp.append("0"); // 补前导0
                }
                tmp.append(Integer.toString(c, 16)); // 转16进制
            }
            else // Unicode字符
            {
                tmp.append("%u"); // 追加%u
                if (c <= 0xfff) // 小于等于0xfff时补0
                {
                    // issue#I49JU8@Gitee
                    tmp.append("0"); // 补前导0
                }
                tmp.append(Integer.toString(c, 16)); // 转16进制
            }
        }
        return tmp.toString(); // 返回编码结果
    }

    /**
     * Escape解码
     * 
     * @param content 被转义的内容
     * @return 解码后的字符串
     */
    public static String decode(String content) // 对Escape编码的文本进行解码
    {
        if (StringUtils.isEmpty(content)) // 内容为空时
        {
            return content; // 直接返回
        }

        StringBuilder tmp = new StringBuilder(content.length()); // 创建缓冲区
        int lastPos = 0, pos = 0; // 上次位置和当前位置
        char ch; // 解码后的字符
        while (lastPos < content.length()) // 遍历整个字符串
        {
            pos = content.indexOf("%", lastPos); // 查找下一个%
            if (pos == lastPos) // %在当前位置
            {
                if (content.charAt(pos + 1) == 'u') // %u开头为Unicode
                {
                    ch = (char) Integer.parseInt(content.substring(pos + 2, pos + 6), 16); // 解析4位16进制
                    tmp.append(ch); // 追加字符
                    lastPos = pos + 6; // 更新位置
                }
                else // %开头为ASCII
                {
                    ch = (char) Integer.parseInt(content.substring(pos + 1, pos + 3), 16); // 解析2位16进制
                    tmp.append(ch); // 追加字符
                    lastPos = pos + 3; // 更新位置
                }
            }
            else // %不在当前位置
            {
                if (pos == -1) // 没有更多%
                {
                    tmp.append(content.substring(lastPos)); // 追加剩余内容
                    lastPos = content.length(); // 更新位置到末尾
                }
                else
                {
                    tmp.append(content.substring(lastPos, pos)); // 追加%之前的内容
                    lastPos = pos; // 更新位置到%
                }
            }
        }
        return tmp.toString(); // 返回解码结果
    }

    public static void main(String[] args) // 测试入口
    {
        String html = "<script>alert(1);</script>"; // 测试XSS字符串
        String escape = EscapeUtil.escape(html); // 转义
        // String html = "<scr<script>ipt>alert(\"XSS\")</scr<script>ipt>";
        // String html = "<123";
        // String html = "123>";
        System.out.println("clean: " + EscapeUtil.clean(html)); // 输出清理结果
        System.out.println("escape: " + escape); // 输出转义结果
        System.out.println("unescape: " + EscapeUtil.unescape(escape)); // 输出反转义结果
    }
}
