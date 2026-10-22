// 包声明
package com.debuff.trade.domain.entity;

// 导入所需类
import java.math.BigDecimal;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 求购单对象 debuff_buy_order
 * 
 * @author B同学
 */
public class BuyOrder extends BaseEntity // 求购单实体类，继承基础实体
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 主键 */
    private Long id;

    /** 求购用户ID */
    private Long userId;

    /** 饰品定义ID */
    private Long skinId;

    /** 求购单价 */
    private BigDecimal price;

    /** 状态(0有效 1已完成 2已取消) */
    private Integer status;

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

    /** 获取求购用户ID */
    public Long getUserId()
    {
        return userId;
    }

    /** 设置求购用户ID */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    /** 获取饰品定义ID */
    public Long getSkinId()
    {
        return skinId;
    }

    /** 设置饰品定义ID */
    public void setSkinId(Long skinId)
    {
        this.skinId = skinId;
    }

    /** 获取求购单价 */
    public BigDecimal getPrice()
    {
        return price;
    }

    /** 设置求购单价 */
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
}
