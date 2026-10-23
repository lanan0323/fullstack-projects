// 包声明
package com.debuff.system.mapper;

// 导入集合类和角色部门关联实体类
import java.util.List;
import com.debuff.system.domain.SysRoleDept;

/**
 * 角色与部门关联表 数据层
 * 
 * @author ruoyi
 */
// 角色部门关联Mapper接口，定义角色与部门关联关系的增删查方法
public interface SysRoleDeptMapper
{
    /**
     * 通过角色ID删除角色和部门关联
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    // 根据角色ID删除该角色的所有部门关联
    public int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 批量删除角色部门关联信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    // 批量删除角色部门关联记录
    public int deleteRoleDept(Long[] ids);

    /**
     * 查询部门使用数量
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    // 统计指定部门被多少个角色关联
    public int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 批量新增角色部门信息
     * 
     * @param roleDeptList 角色部门列表
     * @return 结果
     */
    // 批量插入角色与部门的关联记录
    public int batchRoleDept(List<SysRoleDept> roleDeptList);
}
