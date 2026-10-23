// 包声明
package com.debuff.system.mapper;

// 导入集合类和角色实体类
import java.util.List;
import com.debuff.common.core.domain.entity.SysRole;

/**
 * 角色表 数据层
 * 
 * @author ruoyi
 */
// 角色Mapper接口，定义角色的增删改查方法
public interface SysRoleMapper
{
    /**
     * 根据条件分页查询角色数据
     * 
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    // 根据条件分页查询角色列表
    public List<SysRole> selectRoleList(SysRole role);

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    // 根据用户ID查询该用户拥有的角色列表
    public List<SysRole> selectRolePermissionByUserId(Long userId);

    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    // 查询所有角色记录
    public List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     * 
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    // 根据用户ID查询已分配的角色ID列表
    public List<Long> selectRoleListByUserId(Long userId);

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    // 根据ID查询单条角色详情
    public SysRole selectRoleById(Long roleId);

    /**
     * 根据用户ID查询角色
     * 
     * @param userName 用户名
     * @return 角色列表
     */
    // 根据用户名查询该用户拥有的角色列表
    public List<SysRole> selectRolesByUserName(String userName);

    /**
     * 校验角色名称是否唯一
     * 
     * @param roleName 角色名称
     * @return 角色信息
     */
    // 检查角色名称是否唯一
    public SysRole checkRoleNameUnique(String roleName);

    /**
     * 校验角色权限是否唯一
     * 
     * @param roleKey 角色权限
     * @return 角色信息
     */
    // 检查角色权限标识是否唯一
    public SysRole checkRoleKeyUnique(String roleKey);

    /**
     * 修改角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 修改一条角色记录
    public int updateRole(SysRole role);

    /**
     * 新增角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 新增一条角色记录
    public int insertRole(SysRole role);

    /**
     * 通过角色ID删除角色
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    // 根据ID删除单条角色
    public int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     * 
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    // 批量删除多条角色
    public int deleteRoleByIds(Long[] roleIds);
}
