// 包声明
package com.debuff.trade.domain;

// 导入所需类
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.debuff.common.annotation.Excel;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 交易订单对象 debuff_order
 * 
 * @author debuff
 * @date 2026-08-25
 */
public class Order extends BaseEntity // 订单实体类，继承BaseEntity获得公共字段
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 自增主键 */
    private Long id;

    /** 订单编号 */
    @Excel(name = "订单编号") // Excel导出列名
    private String orderNo;

    /** 饰品ID */
    @Excel(name = "饰品ID")
    private Long skinId;

    /** 关联库存ID(下单时快照) */
    private Long inventoryId;

    /** 关联上架ID(下单时快照) */
    private Long listingId;

    /** 买家ID */
    @Excel(name = "买家ID")
    private Long buyerId;

    /** 卖家ID */
    @Excel(name = "卖家ID")
    private Long sellerId;

    /** 买家昵称(联查) */
    private String buyerName;

    /** 卖家昵称(联查) */
    private String sellerName;

    /** 饰品名称(联查) */
    private String skinName;

    /** 成交价格 */
    @Excel(name = "成交价格")
    private BigDecimal price;

    /** 平台手续费 */
    @Excel(name = "平台手续费")
    private BigDecimal platformFee;

    /** 卖家实收 */
    @Excel(name = "卖家实收")
    private BigDecimal sellerIncome;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd") // JSON日期格式
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;

    /** 取消时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "取消时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date cancelTime;

    /** 设置主键ID */
    public void setId(Long id) 
    {
        this.id = id;
    }

    /** 获取主键ID */
    public Long getId() 
    {
        return id;
    }

    /** 设置订单编号 */
    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    /** 获取订单编号 */
    public String getOrderNo() 
    {
        return orderNo;
    }

    /** 设置饰品ID */
    public void setSkinId(Long skinId) 
    {
        this.skinId = skinId;
    }

    /** 获取饰品ID */
    public Long getSkinId() 
    {
        return skinId;
    }

    /** 设置库存ID */
    public void setInventoryId(Long inventoryId) 
    {
        this.inventoryId = inventoryId;
    }

    /** 获取库存ID */
    public Long getInventoryId() 
    {
        return inventoryId;
    }

    /** 设置上架ID */
    public void setListingId(Long listingId) 
    {
        this.listingId = listingId;
    }

    /** 获取上架ID */
    public Long getListingId() 
    {
        return listingId;
    }

    /** 设置买家ID */
    public void setBuyerId(Long buyerId) 
    {
        this.buyerId = buyerId;
    }

    /** 获取买家ID */
    public Long getBuyerId() 
    {
        return buyerId;
    }

    /** 设置卖家ID */
    public void setSellerId(Long sellerId) 
    {
        this.sellerId = sellerId;
    }

    /** 获取卖家ID */
    public Long getSellerId() 
    {
        return sellerId;
    }

    /** 设置买家昵称 */
    public void setBuyerName(String buyerName) 
    {
        this.buyerName = buyerName;
    }

    /** 获取买家昵称 */
    public String getBuyerName() 
    {
        return buyerName;
    }

    /** 设置卖家昵称 */
    public void setSellerName(String sellerName) 
    {
        this.sellerName = sellerName;
    }

    /** 获取卖家昵称 */
    public String getSellerName() 
    {
        return sellerName;
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

    /** 设置成交价格 */
    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    /** 获取成交价格 */
    public BigDecimal getPrice() 
    {
        return price;
    }

    /** 设置平台手续费 */
    public void setPlatformFee(BigDecimal platformFee) 
    {
        this.platformFee = platformFee;
    }

    /** 获取平台手续费 */
    public BigDecimal getPlatformFee() 
    {
        return platformFee;
    }

    /** 设置卖家实收 */
    public void setSellerIncome(BigDecimal sellerIncome) 
    {
        this.sellerIncome = sellerIncome;
    }

    /** 获取卖家实收 */
    public BigDecimal getSellerIncome() 
    {
        return sellerIncome;
    }

    /** 设置状态 */
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    /** 获取状态 */
    public Integer getStatus() 
    {
        return status;
    }

    /** 设置支付时间 */
    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    /** 获取支付时间 */
    public Date getPayTime() 
    {
        return payTime;
    }

    /** 设置取消时间 */
    public void setCancelTime(Date cancelTime) 
    {
        this.cancelTime = cancelTime;
    }

    /** 获取取消时间 */
    public Date getCancelTime() 
    {
        return cancelTime;
    }

    @Override
    public String toString() { // 转字符串方法（用于日志输出）
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId()) // 主键ID
            .append("orderNo", getOrderNo()) // 订单编号
            .append("skinId", getSkinId()) // 饰品ID
            .append("inventoryId", getInventoryId()) // 库存ID
            .append("listingId", getListingId()) // 上架ID
            .append("buyerId", getBuyerId()) // 买家ID
            .append("buyerName", getBuyerName()) // 买家昵称
            .append("sellerId", getSellerId()) // 卖家ID
            .append("sellerName", getSellerName()) // 卖家昵称
            .append("price", getPrice()) // 成交价格
            .append("platformFee", getPlatformFee()) // 平台手续费
            .append("sellerIncome", getSellerIncome()) // 卖家实收
            .append("status", getStatus()) // 状态
            .append("createTime", getCreateTime()) // 创建时间
            .append("payTime", getPayTime()) // 支付时间
            .append("cancelTime", getCancelTime()) // 取消时间
            .append("updateTime", getUpdateTime()) // 更新时间
            .toString();
    }
}
