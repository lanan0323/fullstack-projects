// 包声明
package com.debuff.trade.domain.vo;

// 导入所需类
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 求购单VO（含饰品关联信息）
 *
 * @author B同学
 */
public class BuyOrderVO
{
    /** 求购单ID */
    private Long id;

    /** 求购用户ID */
    private Long userId;

    /** 求购用户昵称 */
    private String buyerName;

    /** 饰品定义ID */
    private Long skinId;

    /** 饰品名称 */
    private String skinName;

    /** 饰品图片URL */
    private String imageUrl;

    /** 武器名称 */
    private String weaponName;

    /** 品质名称 */
    private String qualityName;

    /** 品质颜色 */
    private String qualityColor;

    /** 求购单价 */
    private BigDecimal price;

    /** 状态(0有效 1已完成 2已取消) */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON日期格式化
    private Date createTime;

    /** 获取求购单ID */
    public Long getId() { return id; }
    /** 设置求购单ID */
    public void setId(Long id) { this.id = id; }

    /** 获取求购用户ID */
    public Long getUserId() { return userId; }
    /** 设置求购用户ID */
    public void setUserId(Long userId) { this.userId = userId; }

    /** 获取求购用户昵称 */
    public String getBuyerName() { return buyerName; }
    /** 设置求购用户昵称 */
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    /** 获取饰品定义ID */
    public Long getSkinId() { return skinId; }
    /** 设置饰品定义ID */
    public void setSkinId(Long skinId) { this.skinId = skinId; }

    /** 获取饰品名称 */
    public String getSkinName() { return skinName; }
    /** 设置饰品名称 */
    public void setSkinName(String skinName) { this.skinName = skinName; }

    /** 获取饰品图片URL */
    public String getImageUrl() { return imageUrl; }
    /** 设置饰品图片URL */
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

    /** 获取求购单价 */
    public BigDecimal getPrice() { return price; }
    /** 设置求购单价 */
    public void setPrice(BigDecimal price) { this.price = price; }

    /** 获取状态 */
    public Integer getStatus() { return status; }
    /** 设置状态 */
    public void setStatus(Integer status) { this.status = status; }

    /** 获取创建时间 */
    public Date getCreateTime() { return createTime; }
    /** 设置创建时间 */
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
