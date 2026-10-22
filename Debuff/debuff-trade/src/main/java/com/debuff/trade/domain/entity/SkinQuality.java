// 包声明
package com.debuff.trade.domain.entity;

// 导入所需类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 饰品品质对象 debuff_skin_quality
 * 
 * @author B同学
 */
public class SkinQuality extends BaseEntity // 继承基础实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 主键 */
    private Long id;

    /** 品质名称 */
    private String qualityName;

    /** 品质对应颜色(十六进制) */
    private String qualityColor;

    /** 排序号 */
    private Integer sortOrder;

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

    /** 获取排序号 */
    public Integer getSortOrder()
    {
        return sortOrder;
    }

    /** 设置排序号 */
    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }
}
