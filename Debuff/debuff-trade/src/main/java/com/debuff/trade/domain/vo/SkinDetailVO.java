// 包声明
package com.debuff.trade.domain.vo;

// 导入所需类
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 饰品详情VO（基本信息 + 在售列表 + 价格历史）
 * 
 * @author B同学
 */
public class SkinDetailVO // 饰品详情视图对象，包含基本信息、在售列表和价格历史
{
    /** 饰品ID */
    private Long skinId;

    /** 饰品名称 */
    private String skinName;

    /** 饰品图片URL */
    private String imageUrl;

    /** 饰品描述 */
    private String description;

    /** 建议最低价格 */
    private BigDecimal minPrice;

    /** 武器类型ID */
    private Long weaponTypeId;

    /** 武器名称 */
    private String weaponName;

    /** 武器分类 */
    private String weaponCategory;

    /** 品质ID */
    private Long qualityId;

    /** 品质名称 */
    private String qualityName;

    /** 品质颜色 */
    private String qualityColor;

    /** 状态(0上架 1下架) */
    private String status;

    /** 当前在售列表 */
    private List<ListingItem> listings;

    /** 最近30天价格历史 */
    private List<PricePoint> priceHistory;

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

    /** 获取饰品图片URL */
    public String getImageUrl()
    {
        return imageUrl;
    }

    /** 设置饰品图片URL */
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

    /** 获取建议最低价格 */
    public BigDecimal getMinPrice()
    {
        return minPrice;
    }

    /** 设置建议最低价格 */
    public void setMinPrice(BigDecimal minPrice)
    {
        this.minPrice = minPrice;
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

    /** 获取在售列表 */
    public List<ListingItem> getListings()
    {
        return listings;
    }

    /** 设置在-in售列表 */
    public void setListings(List<ListingItem> listings)
    {
        this.listings = listings;
    }

    /** 获取价格历史 */
    public List<PricePoint> getPriceHistory()
    {
        return priceHistory;
    }

    /** 设置价格历史 */
    public void setPriceHistory(List<PricePoint> priceHistory)
    {
        this.priceHistory = priceHistory;
    }

    /**
     * 在售饰品条目
     */
    public static class ListingItem // 在售饰品内部类，表示一条在售记录
    {
        /** 在售记录ID */
        private Long id;

        /** 关联库存ID */
        private Long inventoryId;

        /** 卖家用户ID */
        private Long sellerId;

        /** 卖家昵称 */
        private String sellerName;

        /** 售价 */
        private BigDecimal price;

        /** 上架时间 */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 日期格式化
        private Date listingTime;

        /** 获取在售记录ID */
        public Long getId() { return id; }
        /** 设置在售记录ID */
        public void setId(Long id) { this.id = id; }
        /** 获取关联库存ID */
        public Long getInventoryId() { return inventoryId; }
        /** 设置关联库存ID */
        public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }
        /** 获取卖家用户ID */
        public Long getSellerId() { return sellerId; }
        /** 设置卖家用户ID */
        public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
        /** 获取卖家昵称 */
        public String getSellerName() { return sellerName; }
        /** 设置卖家昵称 */
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        /** 获取售价 */
        public BigDecimal getPrice() { return price; }
        /** 设置售价 */
        public void setPrice(BigDecimal price) { this.price = price; }
        /** 获取上架时间 */
        public Date getListingTime() { return listingTime; }
        /** 设置上架时间 */
        public void setListingTime(Date listingTime) { this.listingTime = listingTime; }
    }

    /**
     * 价格历史点
     */
    public static class PricePoint // 价格历史内部类，表示某天的价格数据
    {
        /** 记录日期 */
        private String recordDate;

        /** 当日均价 */
        private BigDecimal avgPrice;

        /** 当日最低价 */
        private BigDecimal minPrice;

        /** 当日最高价 */
        private BigDecimal maxPrice;

        /** 当日成交量 */
        private Integer volume;

        /** 获取记录日期 */
        public String getRecordDate() { return recordDate; }
        /** 设置记录日期 */
        public void setRecordDate(String recordDate) { this.recordDate = recordDate; }
        /** 获取当日均价 */
        public BigDecimal getAvgPrice() { return avgPrice; }
        /** 设置当日均价 */
        public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }
        /** 获取当日最低价 */
        public BigDecimal getMinPrice() { return minPrice; }
        /** 设置当日最低价 */
        public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
        /** 获取当日最高价 */
        public BigDecimal getMaxPrice() { return maxPrice; }
        /** 设置当日最高价 */
        public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
        /** 获取当日成交量 */
        public Integer getVolume() { return volume; }
        /** 设置当日成交量 */
        public void setVolume(Integer volume) { this.volume = volume; }
    }
}
