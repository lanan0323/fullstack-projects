// 包声明
package com.debuff.trade.enums;

// 导入所需类
import java.math.BigDecimal;

/**
 * 磨损等级枚举（磨损度区间：[minValue, maxValue)）
 * 
 * @author B同学
 */
public enum WearLevel // 磨损等级枚举，定义CS2饰品的5种磨损程度
{
    /** 崭新出厂 0.00 - 0.07 */
    FACTORY_NEW("崭新出厂", "Factory New", new BigDecimal("0.00"), new BigDecimal("0.07")),
    /** 略有磨损 0.07 - 0.15 */
    MINIMAL_WEAR("略有磨损", "Minimal Wear", new BigDecimal("0.07"), new BigDecimal("0.15")),
    /** 久经沙场 0.15 - 0.38 */
    FIELD_TESTED("久经沙场", "Field-Tested", new BigDecimal("0.15"), new BigDecimal("0.38")),
    /** 破损不堪 0.38 - 0.45 */
    WELL_WORN("破损不堪", "Well-Worn", new BigDecimal("0.38"), new BigDecimal("0.45")),
    /** 战痕累累 0.45 - 1.00 */
    BATTLE_SCARRED("战痕累累", "Battle-Scarred", new BigDecimal("0.45"), new BigDecimal("1.01"));

    private final String wearName; // 磨损中文名称

    private final String englishName; // 磨损英文名称

    private final BigDecimal minValue; // 磨损区间最小值（含）

    private final BigDecimal maxValue; // 磨损区间最大值（不含）

    WearLevel(String wearName, String englishName, BigDecimal minValue, BigDecimal maxValue) // 枚举构造方法
    {
        this.wearName = wearName;
        this.englishName = englishName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /** 获取磨损中文名称 */
    public String getWearName()
    {
        return wearName;
    }

    /** 获取磨损英文名称 */
    public String getEnglishName()
    {
        return englishName;
    }

    /** 获取磨损区间最小值 */
    public BigDecimal getMinValue()
    {
        return minValue;
    }

    /** 获取磨损区间最大值 */
    public BigDecimal getMaxValue()
    {
        return maxValue;
    }

    /**
     * 根据磨损度数值获取磨损名称
     * 
     * @param floatValue 磨损度数值
     * @return 磨损名称，数值为空或非法时返回空串
     */
    public static String getWearName(BigDecimal floatValue) // 根据磨损度数值匹配对应的磨损等级名称
    {
        if (floatValue == null) // 如果磨损度为空
        {
            return ""; // 返回空串
        }
        for (WearLevel level : values()) // 遍历所有磨损等级
        {
            if (floatValue.compareTo(level.minValue) >= 0 && floatValue.compareTo(level.maxValue) < 0) // 判断数值是否在当前区间内
            {
                return level.wearName; // 返回匹配的磨损名称
            }
        }
        return ""; // 未匹配到任何等级，返回空串
    }
}
