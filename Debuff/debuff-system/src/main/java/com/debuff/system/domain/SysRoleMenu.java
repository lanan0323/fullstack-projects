// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Apache Commons的字符串构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 角色和菜单关联 sys_role_menu
 * 
 * @author ruoyi
 */
public class SysRoleMenu // 角色与菜单的关联关系实体类
{
    /** 角色ID */
    private Long roleId;
    
    /** 菜单ID */
    private Long menuId;

    public Long getRoleId() // 获取角色ID
    {
        return roleId;
    }

    public void setRoleId(Long roleId) // 设置角色ID
    {
        this.roleId = roleId;
    }

    public Long getMenuId() // 获取菜单ID
    {
        return menuId;
    }

    public void setMenuId(Long menuId) // 设置菜单ID
    {
        this.menuId = menuId;
    }

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("roleId", getRoleId()) // 追加角色ID
            .append("menuId", getMenuId()) // 追加菜单ID
            .toString();
    }
}
