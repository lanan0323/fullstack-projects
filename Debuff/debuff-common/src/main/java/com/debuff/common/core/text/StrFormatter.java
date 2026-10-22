// 包声明
package com.debuff.common.core.text;

// 导入字符串工具类
import com.debuff.common.utils.StringUtils;

/**
 * 字符串格式化
 * 
 * @author ruoyi
 */
public class StrFormatter
{
    public static final String EMPTY_JSON = "{}"; // 占位符常量
    public static final char C_BACKSLASH = '\\'; // 转义符常量
    public static final char C_DELIM_START = '{'; // 占位符起始字符
    public static final char C_DELIM_END = '}'; // 占位符结束字符

    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     * 
     * @param strPattern 字符串模板
     * @param argArray 参数列表
     * @return 结果
     */
    public static String format(final String strPattern, final Object... argArray)
    {
        if (StringUtils.isEmpty(strPattern) || StringUtils.isEmpty(argArray)) // 模板或参数为空直接返回模板
        {
            return strPattern;
        }
        final int strPatternLength = strPattern.length(); // 模板长度

        // 初始化定义好的长度以获得更好的性能
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50); // 预分配容量提升性能

        int handledPosition = 0; // 已处理的模板位置
        int delimIndex;// 占位符所在位置
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) // 遍历每个参数
        {
            delimIndex = strPattern.indexOf(EMPTY_JSON, handledPosition); // 查找下一个占位符
            if (delimIndex == -1) // 没有更多占位符
            {
                if (handledPosition == 0) // 从头到尾都没占位符
                {
                    return strPattern; // 直接返回原模板
                }
                else
                { // 字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                    sbuf.append(strPattern, handledPosition, strPatternLength); // 追加剩余文本
                    return sbuf.toString();
                }
            }
            else
            {
                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == C_BACKSLASH) // 占位符前有转义符
                {
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == C_BACKSLASH) // 双转义符,占位符有效
                    {
                        // 转义符之前还有一个转义符，占位符依旧有效
                        sbuf.append(strPattern, handledPosition, delimIndex - 1); // 追加到转义符前
                        sbuf.append(Convert.utf8Str(argArray[argIndex])); // 替换占位符
                        handledPosition = delimIndex + 2; // 跳过占位符
                    }
                    else // 占位符被单转义符转义
                    {
                        // 占位符被转义
                        argIndex--; // 参数索引回退,此参数不消耗
                        sbuf.append(strPattern, handledPosition, delimIndex - 1); // 追加到转义符前
                        sbuf.append(C_DELIM_START); // 输出左花括号
                        handledPosition = delimIndex + 1; // 跳过左花括号
                    }
                }
                else
                {
                    // 正常占位符
                    sbuf.append(strPattern, handledPosition, delimIndex); // 追加占位符前的文本
                    sbuf.append(Convert.utf8Str(argArray[argIndex])); // 替换为参数值
                    handledPosition = delimIndex + 2; // 跳过占位符
                }
            }
        }
        // 加入最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPattern.length()); // 追加剩余模板文本

        return sbuf.toString(); // 返回格式化后的字符串
    }
}
