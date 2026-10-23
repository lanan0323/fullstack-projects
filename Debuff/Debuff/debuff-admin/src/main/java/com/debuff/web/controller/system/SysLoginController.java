// 包声明
package com.debuff.web.controller.system;

// 导入集合、Spring MVC和安全相关依赖
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.constant.Constants;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.entity.SysMenu;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.LoginBody;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.framework.web.service.SysLoginService;
import com.debuff.framework.web.service.SysPermissionService;
import com.debuff.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
// 标记为REST控制器
@RestController
public class SysLoginController
{
    // 注入登录服务
    @Autowired
    private SysLoginService loginService;

    // 注入菜单服务
    @Autowired
    private ISysMenuService menuService;

    // 注入权限服务
    @Autowired
    private SysPermissionService permissionService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        // 构建成功返回结果
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        // 调用登录服务验证用户名、密码、验证码和UUID，返回token
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        // 将token放入返回结果
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        // 获取当前登录用户对象
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        // 获取用户拥有的角色权限标识集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        // 获取用户拥有的菜单权限标识集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        // 构建返回结果
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user); // 用户基本信息
        ajax.put("roles", roles); // 角色标识集合
        ajax.put("permissions", permissions); // 权限标识集合
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        // 获取当前用户ID
        Long userId = SecurityUtils.getUserId();
        // 根据用户ID查询菜单树
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        // 构建前端路由菜单并返回
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
