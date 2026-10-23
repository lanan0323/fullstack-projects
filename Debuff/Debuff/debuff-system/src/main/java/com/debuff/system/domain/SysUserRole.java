// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Apache Commons的字符串构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户和角色关联 sys_user_role
 * 
 * @author ruoyi
 */
public class SysUserRole // 用户与角色的关联关系实体类
{
    /** 用户ID */
    private Long userId;
    
    /** 角色ID */
    private Long roleId;

    public Long getUserId() // 获取用户ID
    {
        return userId;
    }

    public void setUserId(Long userId) // 设置用户ID
    {
        this.userId = userId;
    }

    public Long getRoleId() // 获取角色ID
    {
        return roleId;
    }

    public void setRoleId(Long roleId) // 设置角色ID
    {
        this.roleId = roleId;
    }

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId()) // 追加用户ID
            .append("roleId", getRoleId()) // 追加角色ID
            .toString();
    }
}
