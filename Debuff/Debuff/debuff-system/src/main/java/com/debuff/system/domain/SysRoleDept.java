// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Apache Commons的字符串构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 角色和部门关联 sys_role_dept
 * 
 * @author ruoyi
 */
public class SysRoleDept // 角色与部门的关联关系实体类
{
    /** 角色ID */
    private Long roleId;
    
    /** 部门ID */
    private Long deptId;

    public Long getRoleId() // 获取角色ID
    {
        return roleId;
    }

    public void setRoleId(Long roleId) // 设置角色ID
    {
        this.roleId = roleId;
    }

    public Long getDeptId() // 获取部门ID
    {
        return deptId;
    }

    public void setDeptId(Long deptId) // 设置部门ID
    {
        this.deptId = deptId;
    }

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("roleId", getRoleId()) // 追加角色ID
            .append("deptId", getDeptId()) // 追加部门ID
            .toString();
    }
}
