// 包声明
package com.debuff.system.service.impl;

// 导入相关依赖
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.debuff.common.annotation.DataScope;
import com.debuff.common.constant.UserConstants;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.spring.SpringUtils;
import com.debuff.system.domain.SysRoleDept;
import com.debuff.system.domain.SysRoleMenu;
import com.debuff.system.domain.SysUserRole;
import com.debuff.system.mapper.SysRoleDeptMapper;
import com.debuff.system.mapper.SysRoleMapper;
import com.debuff.system.mapper.SysRoleMenuMapper;
import com.debuff.system.mapper.SysUserRoleMapper;
import com.debuff.system.service.ISysRoleService;

/**
 * 角色 业务层处理
 * 
 * @author ruoyi
 */
@Service // 注册为Spring服务Bean
public class SysRoleServiceImpl implements ISysRoleService
{
    @Autowired // 自动注入角色Mapper
    private SysRoleMapper roleMapper;

    @Autowired // 自动注入角色菜单关联Mapper
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired // 自动注入用户角色关联Mapper
    private SysUserRoleMapper userRoleMapper;

    @Autowired // 自动注入角色部门关联Mapper
    private SysRoleDeptMapper roleDeptMapper;

    /**
     * 根据条件分页查询角色数据
     * 
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    @DataScope(deptAlias = "d") // 数据权限过滤，使用部门别名d
    public List<SysRole> selectRoleList(SysRole role)
    {
        return roleMapper.selectRoleList(role); // 条件查询角色列表
    }

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRolesByUserId(Long userId)
    {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId); // 查询用户已拥有的角色
        List<SysRole> roles = selectRoleAll(); // 查询所有角色
        for (SysRole role : roles) // 遍历所有角色
        {
            for (SysRole userRole : userRoles) // 对比用户已有角色
            {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue())
                {
                    role.setFlag(true); // 标记该角色为用户已拥有
                    break;
                }
            }
        }
        return roles; // 返回带标记的角色列表
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId)
    {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId); // 查询用户角色权限
        Set<String> permsSet = new HashSet<>(); // 用Set去重权限标识
        for (SysRole perm : perms) // 遍历角色列表
        {
            if (StringUtils.isNotNull(perm))
            {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(","))); // 拆分角色权限标识并加入集合
            }
        }
        return permsSet; // 返回去重后的权限标识集合
    }

    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll()
    {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole()); // 通过AOP代理调用以触发数据权限注解
    }

    /**
     * 根据用户ID获取角色选择框列表
     * 
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId)
    {
        return roleMapper.selectRoleListByUserId(userId); // 查询用户关联的角色ID列表
    }

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long roleId)
    {
        return roleMapper.selectRoleById(roleId); // 根据ID查询角色详情
    }

    /**
     * 校验角色名称是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId(); // 获取角色ID，新增时为-1
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName()); // 根据名称查询角色
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE; // 名称已存在，不唯一
        }
        return UserConstants.UNIQUE; // 名称唯一
    }

    /**
     * 校验角色权限是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleKeyUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId(); // 获取角色ID，新增时为-1
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey()); // 根据权限标识查询角色
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE; // 权限标识已存在，不唯一
        }
        return UserConstants.UNIQUE; // 权限标识唯一
    }

    /**
     * 校验角色是否允许操作
     * 
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role)
    {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员角色"); // 禁止操作超级管理员角色
        }
    }

    /**
     * 校验角色是否有数据权限
     * 
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId)
    {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) // 非管理员才校验数据权限
        {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role); // 通过AOP代理查询以触发数据权限
            if (StringUtils.isEmpty(roles))
            {
                throw new ServiceException("没有权限访问角色数据！"); // 无数据权限则抛异常
            }
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId)
    {
        return userRoleMapper.countUserRoleByRoleId(roleId); // 统计该角色关联的用户数量
    }

    /**
     * 新增保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional // 开启事务，保证角色和菜单关联一起提交
    public int insertRole(SysRole role)
    {
        // 新增角色信息
        roleMapper.insertRole(role); // 插入角色基本信息
        return insertRoleMenu(role); // 新增角色与菜单关联
    }

    /**
     * 修改保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional // 开启事务
    public int updateRole(SysRole role)
    {
        // 修改角色信息
        roleMapper.updateRole(role); // 更新角色基本信息
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId()); // 先删除旧的菜单关联
        return insertRoleMenu(role); // 重新新增角色与菜单关联
    }

    /**
     * 修改角色状态
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role)
    {
        return roleMapper.updateRole(role); // 更新角色状态
    }

    /**
     * 修改数据权限信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional // 开启事务
    public int authDataScope(SysRole role)
    {
        // 修改角色信息
        roleMapper.updateRole(role); // 更新角色基本信息
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId()); // 先删除旧的部门关联
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role); // 重新新增角色与部门关联
    }

    /**
     * 新增角色菜单信息
     * 
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role)
    {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>(); // 构建角色菜单关联列表
        for (Long menuId : role.getMenuIds()) // 遍历菜单ID数组
        {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId()); // 设置角色ID
            rm.setMenuId(menuId); // 设置菜单ID
            list.add(rm); // 加入关联列表
        }
        if (list.size() > 0)
        {
            rows = roleMenuMapper.batchRoleMenu(list); // 批量插入角色菜单关联
        }
        return rows; // 返回影响行数
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    public int insertRoleDept(SysRole role)
    {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<SysRoleDept>(); // 构建角色部门关联列表
        for (Long deptId : role.getDeptIds()) // 遍历部门ID数组
        {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId()); // 设置角色ID
            rd.setDeptId(deptId); // 设置部门ID
            list.add(rd); // 加入关联列表
        }
        if (list.size() > 0)
        {
            rows = roleDeptMapper.batchRoleDept(list); // 批量插入角色部门关联
        }
        return rows; // 返回影响行数
    }

    /**
     * 通过角色ID删除角色
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional // 开启事务
    public int deleteRoleById(Long roleId)
    {
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(roleId); // 删除角色菜单关联
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(roleId); // 删除角色部门关联
        return roleMapper.deleteRoleById(roleId); // 删除角色本身
    }

    /**
     * 批量删除角色信息
     * 
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional // 开启事务
    public int deleteRoleByIds(Long[] roleIds)
    {
        for (Long roleId : roleIds) // 遍历每个待删除的角色
        {
            checkRoleAllowed(new SysRole(roleId)); // 校验是否允许操作（超级管理员不可删）
            checkRoleDataScope(roleId); // 校验数据权限
            SysRole role = selectRoleById(roleId); // 查询角色信息
            if (countUserRoleByRoleId(roleId) > 0) // 检查是否有用户关联
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName())); // 有用户关联则禁止删除
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenu(roleIds); // 批量删除角色菜单关联
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDept(roleIds); // 批量删除角色部门关联
        return roleMapper.deleteRoleByIds(roleIds); // 批量删除角色本身
    }

    /**
     * 取消授权用户角色
     * 
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole)
    {
        return userRoleMapper.deleteUserRoleInfo(userRole); // 删除单条用户角色关联
    }

    /**
     * 批量取消授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds)
    {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds); // 批量删除用户角色关联
    }

    /**
     * 批量选择授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds)
    {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<SysUserRole>(); // 构建用户角色关联列表
        for (Long userId : userIds) // 遍历用户ID数组
        {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId); // 设置用户ID
            ur.setRoleId(roleId); // 设置角色ID
            list.add(ur); // 加入关联列表
        }
        return userRoleMapper.batchUserRole(list); // 批量插入用户角色关联
    }
}
