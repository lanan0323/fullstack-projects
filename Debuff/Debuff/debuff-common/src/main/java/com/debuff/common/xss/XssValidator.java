// 包声明
package com.debuff.common.xss;

// 导入字符串工具和JSR303校验器接口
import com.debuff.common.utils.StringUtils;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义xss校验注解实现
 * 
 * @author ruoyi
 */
public class XssValidator implements ConstraintValidator<Xss, String>
{
    private static final String HTML_PATTERN = "<(\\S*?)[^>]*>.*?|<.*? />"; // HTML标签正则表达式

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) // 校验方法
    {
        if (StringUtils.isBlank(value)) // 空白值直接通过校验
        {
            return true;
        }
        return !containsHtml(value); // 不包含HTML标签则通过
    }

    public static boolean containsHtml(String value) // 检查字符串是否包含HTML标签
    {
        Pattern pattern = Pattern.compile(HTML_PATTERN); // 编译正则表达式
        Matcher matcher = pattern.matcher(value); // 匹配字符串
        return matcher.matches(); // 返回是否匹配到HTML标签
    }
}
