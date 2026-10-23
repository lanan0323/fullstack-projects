// 包声明
package com.debuff.system.service;

// 导入集合类、树选择项、菜单实体类和路由VO
import java.util.List;
import java.util.Set;
import com.debuff.common.core.domain.TreeSelect;
import com.debuff.common.core.domain.entity.SysMenu;
import com.debuff.system.domain.vo.RouterVo;

/**
 * 菜单 业务层
 * 
 * @author ruoyi
 */
// 菜单服务接口，定义菜单的增删改查、权限查询及路由构建方法
public interface ISysMenuService
{
    /**
     * 根据用户查询系统菜单列表
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    // 根据用户ID查询菜单列表（简化版）
    public List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     * 
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    // 根据条件和用户ID查询菜单列表
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    // 根据用户ID查询该用户的所有权限标识集合
    public Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    // 根据角色ID查询该角色的权限标识集合
    public Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    // 根据用户ID查询完整的菜单树
    public List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    // 根据角色ID查询该角色已关联的菜单ID列表
    public List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     * 
     * @param menus 菜单列表
     * @return 路由列表
     */
    // 将菜单列表转换为前端路由配置
    public List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     * 
     * @param menus 菜单列表
     * @return 树结构列表
     */
    // 将菜单列表转换为树形结构
    public List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    // 将菜单列表转换为下拉树选择结构
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     * 
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    // 根据ID查询单条菜单详情
    public SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     * 
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    // 检查该菜单下是否还有子菜单
    public boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     * 
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    // 检查该菜单是否被角色关联
    public boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    // 新增一条菜单记录
    public int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    // 修改一条菜单记录
    public int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    // 根据ID删除单条菜单
    public int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    // 检查菜单名称是否唯一
    public boolean checkMenuNameUnique(SysMenu menu);
}
