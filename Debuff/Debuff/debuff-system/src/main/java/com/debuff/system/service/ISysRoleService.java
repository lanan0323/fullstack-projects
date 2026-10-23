// 包声明
package com.debuff.system.service;

// 导入集合类、角色实体类和用户角色关联类
import java.util.List;
import java.util.Set;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.system.domain.SysUserRole;

/**
 * 角色业务层
 * 
 * @author ruoyi
 */
// 角色服务接口，定义角色的增删改查、权限校验及用户授权方法
public interface ISysRoleService
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
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    // 根据用户ID查询该用户拥有的角色列表
    public List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    // 根据用户ID查询该用户的角色权限标识集合
    public Set<String> selectRolePermissionByUserId(Long userId);

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
     * 校验角色名称是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 检查角色名称是否唯一
    public boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 检查角色权限标识是否唯一
    public boolean checkRoleKeyUnique(SysRole role);

    /**
     * 校验角色是否允许操作
     * 
     * @param role 角色信息
     */
    // 校验当前用户是否有权操作该角色（如不能操作超管角色）
    public void checkRoleAllowed(SysRole role);

    /**
     * 校验角色是否有数据权限
     * 
     * @param roleId 角色id
     */
    // 校验当前用户对该角色是否有数据权限
    public void checkRoleDataScope(Long roleId);

    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    // 统计指定角色关联了多少用户
    public int countUserRoleByRoleId(Long roleId);

    /**
     * 新增保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 新增一条角色记录
    public int insertRole(SysRole role);

    /**
     * 修改保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 修改一条角色记录
    public int updateRole(SysRole role);

    /**
     * 修改角色状态
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 修改角色的启用/禁用状态
    public int updateRoleStatus(SysRole role);

    /**
     * 修改数据权限信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    // 修改角色的数据权限范围
    public int authDataScope(SysRole role);

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

    /**
     * 取消授权用户角色
     * 
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    // 取消单个用户的角色授权
    public int deleteAuthUser(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    // 批量取消指定角色下多个用户的授权
    public int deleteAuthUsers(Long roleId, Long[] userIds);

    /**
     * 批量选择授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    // 批量为指定角色授权多个用户
    public int insertAuthUsers(Long roleId, Long[] userIds);
}
