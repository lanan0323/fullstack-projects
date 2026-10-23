// 包声明
package com.debuff.common.utils;

// 导入日期和时间处理相关类
import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间工具类
 * 
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy"; // 年格式

    public static String YYYY_MM = "yyyy-MM"; // 年月格式

    public static String YYYY_MM_DD = "yyyy-MM-dd"; // 年月日格式

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss"; // 紧凑时间戳格式

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"; // 完整日期时间格式

    private static String[] parsePatterns = { // 支持解析的日期格式数组
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate() // 获取当前时间
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     * 
     * @return String
     */
    public static String getDate() // 获取当前日期字符串(yyyy-MM-dd)
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() // 获取当前时间字符串(yyyy-MM-dd HH:mm:ss)
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() // 获取当前时间紧凑格式(yyyyMMddHHmmss)
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) // 按指定格式获取当前时间
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) // 将日期格式化为yyyy-MM-dd
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) // 按指定格式格式化日期
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) // 将字符串按指定格式解析为Date
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e); // 解析失败抛运行时异常
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() // 获取日期路径格式(yyyy/MM/dd)用于文件存储
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() // 获取日期紧凑格式(yyyyMMdd)用于文件存储
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) // 尝试多种格式解析日期字符串
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns); // 用预定义格式数组解析
        }
        catch (ParseException e)
        {
            return null; // 解析失败返回null
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() // 获取JVM启动时间
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算相差天数
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) // 计算两个日期的天数差
    {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * 计算时间差
     *
     * @param endDate 最后时间
     * @param startTime 开始时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(Date endDate, Date startTime) // 计算时间差并返回可读字符串
    {
        long nd = 1000 * 24 * 60 * 60; // 一天的毫秒数
        long nh = 1000 * 60 * 60; // 一小时的毫秒数
        long nm = 1000 * 60; // 一分钟的毫秒数
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor) // LocalDateTime转Date
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor) // LocalDate转Date(时间部分为00:00:00)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }
}
