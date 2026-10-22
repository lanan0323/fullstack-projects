// 包声明
package com.debuff.system.mapper;

// 导入集合类、参数注解和用户角色关联实体类
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.debuff.system.domain.SysUserRole;

/**
 * 用户与角色关联表 数据层
 * 
 * @author ruoyi
 */
// 用户角色关联Mapper接口，定义用户与角色关联关系的增删查方法
public interface SysUserRoleMapper
{
    /**
     * 通过用户ID删除用户和角色关联
     * 
     * @param userId 用户ID
     * @return 结果
     */
    // 根据用户ID删除该用户的所有角色关联
    public int deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户和角色关联
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    // 批量删除用户角色关联记录
    public int deleteUserRole(Long[] ids);

    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    // 统计指定角色关联了多少用户
    public int countUserRoleByRoleId(Long roleId);

    /**
     * 批量新增用户角色信息
     * 
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    // 批量插入用户与角色的关联记录
    public int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 删除用户和角色关联信息
     * 
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    // 根据用户ID和角色ID删除单条关联
    public int deleteUserRoleInfo(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    // 批量取消指定角色下多个用户的授权
    public int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);
}
