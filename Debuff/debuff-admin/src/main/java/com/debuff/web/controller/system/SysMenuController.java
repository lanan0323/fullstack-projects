// 包声明
package com.debuff.web.controller.system;

// 导入集合、Spring Security权限、验证、Spring MVC和日志等依赖
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Log;
import com.debuff.common.constant.UserConstants;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.entity.SysMenu;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.utils.StringUtils;
import com.debuff.system.service.ISysMenuService;

/**
 * 菜单信息
 * 
 * @author ruoyi
 */
// 标记为REST控制器，映射路径为/system/menu，继承BaseController获取通用方法
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    // 注入菜单服务
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    // 需要system:menu:list权限
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public AjaxResult list(SysMenu menu)
    {
        // 查询菜单列表，传入当前用户ID进行数据过滤
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    // 需要system:menu:query权限
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId)
    {
        // 根据菜单ID查询详情
        return success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu menu)
    {
        // 查询菜单列表并构建成下拉树结构
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        // 查询当前用户的菜单列表
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        AjaxResult ajax = AjaxResult.success();
        // 返回该角色已勾选的菜单ID集合
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        // 返回菜单树结构
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    // 需要system:menu:add权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysMenu menu)
    {
        // 校验菜单名称是否唯一
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        // 如果是外链但地址不是http(s)开头
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        // 设置创建人
        menu.setCreateBy(getUsername());
        // 执行新增操作并返回结果
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    // 需要system:menu:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysMenu menu)
    {
        // 校验菜单名称是否唯一
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        // 如果是外链但地址不是http(s)开头
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        // 上级菜单不能选择自己
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        // 设置修改人
        menu.setUpdateBy(getUsername());
        // 执行修改操作并返回结果
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    // 需要system:menu:remove权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") Long menuId)
    {
        // 检查是否存在子菜单
        if (menuService.hasChildByMenuId(menuId))
        {
            return warn("存在子菜单,不允许删除");
        }
        // 检查菜单是否已分配给角色
        if (menuService.checkMenuExistRole(menuId))
        {
            return warn("菜单已分配,不允许删除");
        }
        // 执行删除操作
        return toAjax(menuService.deleteMenuById(menuId));
    }
}
