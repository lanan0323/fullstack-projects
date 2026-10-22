// 包声明
package com.debuff.trade.domain.vo;

// 导入所需类
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 用户库存VO（含饰品关联信息）
 * 
 * @author B同学
 */
public class InventoryVO // 库存视图对象（包含饰品关联数据）
{
    /** 库存ID */
    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 关联饰品定义ID */
    private Long skinId;

    /** 饰品名称 */
    private String skinName;

    /** 饰品图片URL */
    private String imageUrl;

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

    /** 磨损度数值 */
    private BigDecimal floatValue;

    /** 磨损名称 */
    private String wearName;

    /** 贴纸信息(JSON) */
    private String stickerInfo;

    /** 游戏内检视链接 */
    private String inspectLink;

    /** 来源(purchase/upload/trade) */
    private String source;

    /** 状态(0在库 1在售 2冻结) */
    private Integer status;

    /** 获得时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON序列化日期格式
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON序列化日期格式
    private Date updateTime;

    /** 获取库存ID */
    public Long getId()
    {
        return id;
    }

    /** 设置库存ID */
    public void setId(Long id)
    {
        this.id = id;
    }

    /** 获取用户ID */
    public Long getUserId()
    {
        return userId;
    }

    /** 设置用户ID */
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

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

    /** 获取磨损度 */
    public BigDecimal getFloatValue()
    {
        return floatValue;
    }

    /** 设置磨损度 */
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

    /** 获取检视链接 */
    public String getInspectLink()
    {
        return inspectLink;
    }

    /** 设置检视链接 */
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

    /** 获取创建时间 */
    public Date getCreateTime()
    {
        return createTime;
    }

    /** 设置创建时间 */
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    /** 获取更新时间 */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    /** 设置更新时间 */
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
