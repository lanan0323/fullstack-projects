// 包声明
package com.debuff.trade.domain.vo;

// 导入所需类
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 收藏列表VO（含饰品和挂单信息）
 * 
 * @author B同学
 */
public class FavoriteVO // 收藏视图对象
{
    /** 收藏ID */
    private Long id;

    /** 挂单ID */
    private Long listingId;

    /** 饰品ID */
    private Long skinId;

    /** 饰品名称 */
    private String skinName;

    /** 饰品图片 */
    private String imageUrl;

    /** 武器名称 */
    private String weaponName;

    /** 品质名称 */
    private String qualityName;

    /** 品质颜色 */
    private String qualityColor;

    /** 挂单价格 */
    private BigDecimal price;

    /** 挂单状态（0在售 1已售 2已下架） */
    private String listingStatus;

    /** 收藏时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON日期格式
    private Date createTime;

    /** 获取收藏ID */
    public Long getId() { return id; }
    /** 设置收藏ID */
    public void setId(Long id) { this.id = id; }

    /** 获取挂单ID */
    public Long getListingId() { return listingId; }
    /** 设置挂单ID */
    public void setListingId(Long listingId) { this.listingId = listingId; }

    /** 获取饰品ID */
    public Long getSkinId() { return skinId; }
    /** 设置饰品ID */
    public void setSkinId(Long skinId) { this.skinId = skinId; }

    /** 获取饰品名称 */
    public String getSkinName() { return skinName; }
    /** 设置饰品名称 */
    public void setSkinName(String skinName) { this.skinName = skinName; }

    /** 获取图片URL */
    public String getImageUrl() { return imageUrl; }
    /** 设置图片URL */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /** 获取武器名称 */
    public String getWeaponName() { return weaponName; }
    /** 设置武器名称 */
    public void setWeaponName(String weaponName) { this.weaponName = weaponName; }

    /** 获取品质名称 */
    public String getQualityName() { return qualityName; }
    /** 设置品质名称 */
    public void setQualityName(String qualityName) { this.qualityName = qualityName; }

    /** 获取品质颜色 */
    public String getQualityColor() { return qualityColor; }
    /** 设置品质颜色 */
    public void setQualityColor(String qualityColor) { this.qualityColor = qualityColor; }

    /** 获取挂单价格 */
    public BigDecimal getPrice() { return price; }
    /** 设置挂单价格 */
    public void setPrice(BigDecimal price) { this.price = price; }

    /** 获取挂单状态 */
    public String getListingStatus() { return listingStatus; }
    /** 设置挂单状态 */
    public void setListingStatus(String listingStatus) { this.listingStatus = listingStatus; }

    /** 获取收藏时间 */
    public Date getCreateTime() { return createTime; }
    /** 设置收藏时间 */
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
