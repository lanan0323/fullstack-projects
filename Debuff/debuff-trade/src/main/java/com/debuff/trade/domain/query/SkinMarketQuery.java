// 包声明
package com.debuff.trade.domain.query;

// 导入所需类
import java.math.BigDecimal;

/**
 * 饰品市场列表查询条件
 * 
 * @author B同学
 */
public class SkinMarketQuery // 饰品市场查询参数封装类
{
    /** 武器分类(rifle/pistol/smg/sniper/shotgun/knife/glove) */
    private String weaponCategoryId;

    /** 武器类型ID */
    private Long weaponTypeId;

    /** 品质ID */
    private Long qualityId;

    /** 最低价格 */
    private BigDecimal minPrice;

    /** 最高价格 */
    private BigDecimal maxPrice;

    /** 关键词（饰品名称模糊搜索） */
    private String keyword;

    /** 排序方式 price_asc/price_desc/newest */
    private String orderBy;

    /** 获取武器分类编码 */
    public String getWeaponCategoryId()
    {
        return weaponCategoryId;
    }

    /** 设置武器分类编码 */
    public void setWeaponCategoryId(String weaponCategoryId)
    {
        this.weaponCategoryId = weaponCategoryId;
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

    /** 获取最低价格 */
    public BigDecimal getMinPrice()
    {
        return minPrice;
    }

    /** 设置最低价格 */
    public void setMinPrice(BigDecimal minPrice)
    {
        this.minPrice = minPrice;
    }

    /** 获取最高价格 */
    public BigDecimal getMaxPrice()
    {
        return maxPrice;
    }

    /** 设置最高价格 */
    public void setMaxPrice(BigDecimal maxPrice)
    {
        this.maxPrice = maxPrice;
    }

    /** 获取搜索关键词 */
    public String getKeyword()
    {
        return keyword;
    }

    /** 设置搜索关键词 */
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    /** 获取排序方式 */
    public String getOrderBy()
    {
        return orderBy;
    }

    /** 设置排序方式 */
    public void setOrderBy(String orderBy)
    {
        this.orderBy = orderBy;
    }
}
