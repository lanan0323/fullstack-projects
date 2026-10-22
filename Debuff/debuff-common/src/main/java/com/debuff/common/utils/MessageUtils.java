// 包声明
package com.debuff.common.utils;

// 导入Spring消息源和上下文相关类
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.debuff.common.utils.spring.SpringUtils;

/**
 * 获取i18n资源文件
 * 
 * @author ruoyi
 */
public class MessageUtils
{
    /**
     * 根据消息键和参数 获取消息 委托给spring messageSource
     *
     * @param code 消息键
     * @param args 参数
     * @return 获取国际化翻译值
     */
    public static String message(String code, Object... args) // 根据消息键获取国际化消息
    {
        MessageSource messageSource = SpringUtils.getBean(MessageSource.class); // 获取Spring MessageSource Bean
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale()); // 根据当前语言环境获取消息
    }
}
