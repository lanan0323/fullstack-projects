// 包声明
package com.debuff.trade.domain.entity;

// 导入所需类
import java.math.BigDecimal;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 用户库存对象 debuff_inventory
 * 
 * @author B同学
 */
public class Inventory extends BaseEntity // 继承基础实体类，获得通用字段
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 主键 */
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 关联饰品定义ID */
    private Long skinId;

    /** 磨损度数值(0.00~1.00) */
    private BigDecimal floatValue;

    /** 磨损名称(自动生成) */
    private String wearName;

    /** 贴纸信息(JSON格式) */
    private String stickerInfo;

    /** 游戏内检视链接 */
    private String inspectLink;

    /** 来源(purchase/upload/trade) */
    private String source;

    /** 状态(0在库 1在售 2冻结) */
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

    /** 获取所属用户ID */
    public Long getUserId()
    {
        return userId;
    }

    /** 设置所属用户ID */
    public void setUserId(Long userId)
    {
        this.userId = userId;
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

    /** 获取磨损度数值 */
    public BigDecimal getFloatValue()
    {
        return floatValue;
    }

    /** 设置磨损度数值 */
    public void setFloatValue(BigDecimal floatValue)
    {
        this.floatValue = floatValue;
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

    /** 获取贴纸信息 */
    public String getStickerInfo()
    {
        return stickerInfo;
    }

    /** 设置贴纸信息 */
    public void setStickerInfo(String stickerInfo)
    {
        this.stickerInfo = stickerInfo;
    }

    /** 获取游戏内检视链接 */
    public String getInspectLink()
    {
        return inspectLink;
    }

    /** 设置游戏内检视链接 */
    public void setInspectLink(String inspectLink)
    {
        this.inspectLink = inspectLink;
    }

    /** 获取来源 */
    public String getSource()
    {
        return source;
    }

    /** 设置来源 */
    public void setSource(String source)
    {
        this.source = source;
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
