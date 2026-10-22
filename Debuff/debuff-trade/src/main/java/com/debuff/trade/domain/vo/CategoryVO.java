// 包声明
package com.debuff.trade.domain.vo;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.entity.WeaponType;

/**
 * 市场分类VO（武器分类 + 该分类下的武器列表）
 * 
 * @author B同学
 */
public class CategoryVO // 分类视图对象，用于前端展示武器分类及下属武器
{
    /** 武器分类编码(rifle/pistol/smg/sniper/shotgun/knife/glove) */
    private String weaponCategory;

    /** 武器分类中文名 */
    private String categoryName;

    /** 该分类下的武器类型列表 */
    private List<WeaponType> weaponTypes;

    /** 获取武器分类编码 */
    public String getWeaponCategory()
    {
        return weaponCategory;
    }

    /** 设置武器分类编码 */
    public void setWeaponCategory(String weaponCategory)
    {
        this.weaponCategory = weaponCategory;
    }

    /** 获取分类中文名 */
    public String getCategoryName()
    {
        return categoryName;
    }

    /** 设置分类中文名 */
    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    /** 获取该分类下的武器类型列表 */
    public List<WeaponType> getWeaponTypes()
    {
        return weaponTypes;
    }

    /** 设置该分类下的武器类型列表 */
    public void setWeaponTypes(List<WeaponType> weaponTypes)
    {
        this.weaponTypes = weaponTypes;
    }
}
