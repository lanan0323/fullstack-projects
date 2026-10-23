// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.trade.domain.entity.WeaponType;
import com.debuff.trade.mapper.WeaponTypeMapper;
import com.debuff.trade.service.IWeaponTypeService;

/**
 * 武器类型 服务层实现
 * 
 * @author B同学
 */
@Service // 标记为Spring服务组件
public class WeaponTypeServiceImpl implements IWeaponTypeService // 实现武器类型服务接口
{
    @Autowired // 自动注入武器类型Mapper
    private WeaponTypeMapper weaponTypeMapper;

    @Override // 查询武器类型列表
    public List<WeaponType> selectWeaponTypeList(WeaponType weaponType)
    {
        return weaponTypeMapper.selectWeaponTypeList(weaponType); // 委托给Mapper执行查询
    }

    @Override // 查询全部武器类型
    public List<WeaponType> selectAllWeaponTypes()
    {
        return weaponTypeMapper.selectAllWeaponTypes(); // 委托给Mapper执行查询
    }

    @Override // 根据ID查询武器类型
    public WeaponType selectWeaponTypeById(Long id)
    {
        return weaponTypeMapper.selectWeaponTypeById(id); // 委托给Mapper执行查询
    }

    @Override // 新增武器类型
    public int insertWeaponType(WeaponType weaponType)
    {
        return weaponTypeMapper.insertWeaponType(weaponType); // 委托给Mapper执行新增
    }

    @Override // 修改武器类型
    public int updateWeaponType(WeaponType weaponType)
    {
        return weaponTypeMapper.updateWeaponType(weaponType); // 委托给Mapper执行更新
    }

    @Override // 批量删除武器类型
    public int deleteWeaponTypeByIds(Long[] ids)
    {
        return weaponTypeMapper.deleteWeaponTypeByIds(ids); // 委托给Mapper执行批量删除
    }
}
