// 包声明
package com.debuff.trade.domain.entity;

// 导入所需类
import java.math.BigDecimal;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 饰品定义对象 debuff_skin
 * 
 * @author B同学
 */
public class Skin extends BaseEntity // 饰品实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 主键 */
    private Long id;

    /** 饰品名称 */
    private String skinName;

    /** 关联武器类型ID */
    private Long weaponTypeId;

    /** 关联品质ID */
    private Long qualityId;

    /** 饰品图片URL */
    private String imageUrl;

    /** 饰品描述 */
    private String description;

    /** 建议最低价格 */
    private BigDecimal minPrice;

    /** 状态(0上架 1下架) */
    private String status;

    /** 武器名称（联查字段，非表字段） */
    private String weaponName;

    /** 武器分类（联查字段，非表字段） */
    private String weaponCategory;

    /** 品质名称（联查字段，非表字段） */
    private String qualityName;

    /** 磨损名称（从库存表联查，非表字段） */
    private String wearName;

    /** 默认磨损度（用于批量同步库存，非表字段） */
    private java.math.BigDecimal floatValue;

    /** 是否仅显示有在售挂单的饰品（管理端筛选，非表字段） */
    private Boolean inMarket;

    /** 获取主键ID */
    public Long getId()
    {
        return id;
    }

    /** 设置主键ID */
    public void setId(Long id)
    {
        this.id = id;
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

    /** 获取武器类型ID */
    public Long getWeaponTypeId()
    {
        return weaponTypeId;
    }

    /** 设置武器类型ID */
    public void setWeaponTypeId(Long weaponTypeId)
    {
        this.weaponTypeId = weaponTypeId;
    }

    /** 获取品质ID */
    public Long getQualityId()
    {
        return qualityId;
    }

    /** 设置品质ID */
    public void setQualityId(Long qualityId)
    {
        this.qualityId = qualityId;
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

    /** 获取饰品描述 */
    public String getDescription()
    {
        return description;
    }

    /** 设置饰品描述 */
    public void setDescription(String description)
    {
        this.description = description;
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

    /** 获取状态 */
    public String getStatus()
    {
        return status;
    }

    /** 设置状态 */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /** 获取武器名称（联查字段） */
    public String getWeaponName()
    {
        return weaponName;
    }

    /** 设置武器名称 */
    public void setWeaponName(String weaponName)
    {
        this.weaponName = weaponName;
    }

    /** 获取武器分类（联查字段） */
    public String getWeaponCategory()
    {
        return weaponCategory;
    }

    /** 设置武器分类 */
    public void setWeaponCategory(String weaponCategory)
    {
        this.weaponCategory = weaponCategory;
    }

    /** 获取品质名称（联查字段） */
    public String getQualityName()
    {
        return qualityName;
    }

    /** 设置品质名称 */
    public void setQualityName(String qualityName)
    {
        this.qualityName = qualityName;
    }

    /** 获取磨损名称（联查字段） */
    public String getWearName()
    {
        return wearName;
    }

    /** 设置磨损名称 */
    public void setWearName(String wearName)
    {
        this.wearName = wearName;
    }

    /** 获取默认磨损度 */
    public java.math.BigDecimal getFloatValue()
    {
        return floatValue;
    }

    /** 设置默认磨损度 */
    public void setFloatValue(java.math.BigDecimal floatValue)
    {
        this.floatValue = floatValue;
    }

    /** 获取是否有在售 */
    public Boolean getInMarket()
    {
        return inMarket;
    }

    /** 设置是否有在售 */
    public void setInMarket(Boolean inMarket)
    {
        this.inMarket = inMarket;
    }
}
