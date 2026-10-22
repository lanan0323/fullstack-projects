// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.entity.WeaponType;

/**
 * 武器类型 数据层
 * 
 * @author B同学
 */
public interface WeaponTypeMapper // 武器类型Mapper接口，定义数据库操作方法
{
    /**
     * 查询武器类型列表
     * 
     * @param weaponType 武器类型
     * @return 武器类型集合
     */
    public List<WeaponType> selectWeaponTypeList(WeaponType weaponType);

    /**
     * 查询全部武器类型（按排序号）
     * 
     * @return 武器类型集合
     */
    public List<WeaponType> selectAllWeaponTypes();

    /**
     * 通过ID查询武器类型
     * 
     * @param id 武器类型ID
     * @return 武器类型
     */
    public WeaponType selectWeaponTypeById(Long id);

    /**
     * 新增武器类型
     * 
     * @param weaponType 武器类型
     * @return 结果
     */
    public int insertWeaponType(WeaponType weaponType);

    /**
     * 修改武器类型
     * 
     * @param weaponType 武器类型
     * @return 结果
     */
    public int updateWeaponType(WeaponType weaponType);

    /**
     * 删除武器类型
     * 
     * @param id 武器类型ID
     * @return 结果
     */
    public int deleteWeaponTypeById(Long id);

    /**
     * 批量删除武器类型
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteWeaponTypeByIds(Long[] ids);
}
