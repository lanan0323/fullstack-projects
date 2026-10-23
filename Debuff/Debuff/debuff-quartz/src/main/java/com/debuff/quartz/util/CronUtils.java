// 包声明
package com.debuff.quartz.util;

// 导入所需类
import java.text.ParseException;
import java.util.Date;
import org.quartz.CronExpression;

/**
 * cron表达式工具类
 * 
 * @author ruoyi
 *
 */
public class CronUtils // cron表达式工具类
{
    /**
     * 返回一个布尔值代表一个给定的Cron表达式的有效性
     *
     * @param cronExpression Cron表达式
     * @return boolean 表达式是否有效
     */
    public static boolean isValid(String cronExpression) // 校验cron表达式是否有效
    {
        return CronExpression.isValidExpression(cronExpression); // 使用Quartz内置校验
    }

    /**
     * 返回一个字符串值,表示该消息无效Cron表达式给出有效性
     *
     * @param cronExpression Cron表达式
     * @return String 无效时返回表达式错误描述,如果有效返回null
     */
    public static String getInvalidMessage(String cronExpression) // 获取无效表达式的错误信息
    {
        try
        {
            new CronExpression(cronExpression); // 尝试解析表达式
            return null; // 有效则返回null
        }
        catch (ParseException pe) // 解析失败
        {
            return pe.getMessage(); // 返回错误描述
        }
    }

    /**
     * 返回下一个执行时间根据给定的Cron表达式
     *
     * @param cronExpression Cron表达式
     * @return Date 下次Cron表达式执行时间
     */
    public static Date getNextExecution(String cronExpression) // 获取下次执行时间
    {
        try
        {
            CronExpression cron = new CronExpression(cronExpression); // 创建Cron表达式对象
            return cron.getNextValidTimeAfter(new Date(System.currentTimeMillis())); // 计算下次有效执行时间
        }
        catch (ParseException e) // 解析异常
        {
            throw new IllegalArgumentException(e.getMessage()); // 抛出非法参数异常
        }
    }
}
