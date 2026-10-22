// 包声明
package com.debuff.trade.domain.vo;

// 导入所需类
import java.math.BigDecimal;

/**
 * 饰品市场列表VO
 * 
 * @author B同学
 */
public class SkinMarketVO // 饰品市场列表视图对象
{
    /** 饰品ID */
    private Long skinId;

    /** 饰品名称 */
    private String skinName;

    /** 饰品图片URL */
    private String imageUrl;

    /** 建议最低价格 */
    private BigDecimal minPrice;

    /** 武器名称 */
    private String weaponName;

    /** 武器分类 */
    private String weaponCategory;

    /** 品质名称 */
    private String qualityName;

    /** 品质颜色 */
    private String qualityColor;

    /** 当前最低售价 */
    private BigDecimal lowestPrice;

    /** 在售数量 */
    private Long listingCount;

    /** 磨损名称（从库存表联查） */
    private String wearName;

    /** 第一个在售挂单ID（用于收藏） */
    private Long firstListingId;

    /** 获取饰品ID */
    public Long getSkinId()
    {
        return skinId;
    }

    /** 设置饰品ID */
    public void setSkinId(Long skinId)
    {
        this.skinId = skinId;
    }

    /** 获取饰品名称 */
    public String getSkinName()
    {
        return skinName;
    }

    /** 设置饰品名称 */
    public void setSkinName(String skinName)
    {
        this.skinName = skinName;
    }

    /** 获取图片URL */
    public String getImageUrl()
    {
        return imageUrl;
    }

    /** 设置图片URL */
    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    /** 获取建议最低价 */
    public BigDecimal getMinPrice()
    {
        return minPrice;
    }

    /** 设置建议最低价 */
    public void setMinPrice(BigDecimal minPrice)
    {
        this.minPrice = minPrice;
    }

    /** 获取武器名称 */
    public String getWeaponName()
    {
        return weaponName;
    }

    /** 设置武器名称 */
    public void setWeaponName(String weaponName)
    {
        this.weaponName = weaponName;
    }

    /** 获取武器分类 */
    public String getWeaponCategory()
    {
        return weaponCategory;
    }

    /** 设置武器分类 */
    public void setWeaponCategory(String weaponCategory)
    {
        this.weaponCategory = weaponCategory;
    }

    /** 获取品质名称 */
    public String getQualityName()
    {
        return qualityName;
    }

    /** 设置品质名称 */
    public void setQualityName(String qualityName)
    {
        this.qualityName = qualityName;
    }

    /** 获取品质颜色 */
    public String getQualityColor()
    {
        return qualityColor;
    }

    /** 设置品质颜色 */
    public void setQualityColor(String qualityColor)
    {
        this.qualityColor = qualityColor;
    }

    /** 获取最低售价 */
    public BigDecimal getLowestPrice()
    {
        return lowestPrice;
    }

    /** 设置最低售价 */
    public void setLowestPrice(BigDecimal lowestPrice)
    {
        this.lowestPrice = lowestPrice;
    }

    /** 获取在售数量 */
    public Long getListingCount()
    {
        return listingCount;
    }

    /** 设置在在售数量 */
    public void setListingCount(Long listingCount)
    {
        this.listingCount = listingCount;
    }

    /** 获取磨损名称 */
    public String getWearName()
    {
        return wearName;
    }

    /** 设置磨损名称 */
    public void setWearName(String wearName)
    {
        this.wearName = wearName;
    }

    /** 获取首个挂单ID */
    public Long getFirstListingId()
    {
        return firstListingId;
    }

    /** 设置首个挂单ID */
    public void setFirstListingId(Long firstListingId)
    {
        this.firstListingId = firstListingId;
    }
}
