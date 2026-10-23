// 包声明
package com.debuff.trade.domain;

// 导入所需类
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 在售饰品对象 debuff_skin_listing
 * 
 * @author debuff
 * @date 2026-08-26
 */
public class Listing extends BaseEntity // 在售饰品实体类，继承基础实体
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 主键 */
    private Long id;

    /** 关联库存ID */
    private Long inventoryId;

    /** 卖家用户ID */
    private Long sellerId;

    /** 关联饰品定义ID(冗余) */
    private Long skinId;

    /** 售价 */
    private BigDecimal price;

    /** 状态(0在售 1已售 2已下架 3已取消) */
    private Integer status;

    /** 上架时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 日期格式化
    private Date listingTime;

    /** 售出时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 日期格式化
    private Date soldTime;

    /** 自动下架时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 日期格式化
    private Date expireTime;

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

    /** 获取关联库存ID */
    public Long getInventoryId()
    {
        return inventoryId;
    }

    /** 设置关联库存ID */
    public void setInventoryId(Long inventoryId)
    {
        this.inventoryId = inventoryId;
    }

    /** 获取卖家用户ID */
    public Long getSellerId()
    {
        return sellerId;
    }

    /** 设置卖家用户ID */
    public void setSellerId(Long sellerId)
    {
        this.sellerId = sellerId;
    }

    /** 获取关联饰品定义ID */
    public Long getSkinId()
    {
        return skinId;
    }

    /** 设置关联饰品定义ID */
    public void setSkinId(Long skinId)
    {
        this.skinId = skinId;
    }

    /** 获取售价 */
    public BigDecimal getPrice()
    {
        return price;
    }

    /** 设置售价 */
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    /** 获取状态 */
    public Integer getStatus()
    {
        return status;
    }

    /** 设置状态 */
    public void setStatus(Integer status)
    {
        this.status = status;
    }

    /** 获取上架时间 */
    public Date getListingTime()
    {
        return listingTime;
    }

    /** 设置上架时间 */
    public void setListingTime(Date listingTime)
    {
        this.listingTime = listingTime;
    }

    /** 获取售出时间 */
    public Date getSoldTime()
    {
        return soldTime;
    }

    /** 设置售出时间 */
    public void setSoldTime(Date soldTime)
    {
        this.soldTime = soldTime;
    }

    /** 获取自动下架时间 */
    public Date getExpireTime()
    {
        return expireTime;
    }

    /** 设置自动下架时间 */
    public void setExpireTime(Date expireTime)
    {
        this.expireTime = expireTime;
    }
}
