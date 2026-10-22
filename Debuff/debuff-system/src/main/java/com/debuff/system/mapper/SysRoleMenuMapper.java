// 包声明
package com.debuff.system.mapper;

// 导入集合类和角色菜单关联实体类
import java.util.List;
import com.debuff.system.domain.SysRoleMenu;

/**
 * 角色与菜单关联表 数据层
 * 
 * @author ruoyi
 */
// 角色菜单关联Mapper接口，定义角色与菜单关联关系的增删查方法
public interface SysRoleMenuMapper
{
    /**
     * 查询菜单使用数量
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    // 统计指定菜单被多少个角色关联
    public int checkMenuExistRole(Long menuId);

    /**
     * 通过角色ID删除角色和菜单关联
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    // 根据角色ID删除该角色的所有菜单关联
    public int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 批量删除角色菜单关联信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    // 批量删除角色菜单关联记录
    public int deleteRoleMenu(Long[] ids);

    /**
     * 批量新增角色菜单信息
     * 
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    // 批量插入角色与菜单的关联记录
    public int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
