// 包声明
package com.debuff.trade.domain.entity;

// 导入所需类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 武器类型对象 debuff_weapon_type
 * 
 * @author B同学
 */
public class WeaponType extends BaseEntity // 继承基础实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 主键 */
    private Long id;

    /** 武器名称 */
    private String weaponName;

    /** 武器分类(rifle/pistol/smg/sniper/shotgun/knife/glove) */
    private String weaponCategory;

    /** 武器图标URL */
    private String icon;

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

    /** 获取武器图标URL */
    public String getIcon()
    {
        return icon;
    }

    /** 设置武器图标URL */
    public void setIcon(String icon)
    {
        this.icon = icon;
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
