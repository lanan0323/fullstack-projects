// 包声明：系统实体类所在包
package com.debuff.common.core.domain.entity;

// 导入Set接口
import java.util.Set;
// 导入JSR303校验注解
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
// 导入Apache Commons的ToString构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入Excel导出注解
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
// 导入实体基类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 角色表 sys_role
 * 
 * @author ruoyi
 */
public class SysRole extends BaseEntity // 角色实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 角色ID */
    @Excel(name = "角色序号", cellType = ColumnType.NUMERIC) // Excel导出配置
    private Long roleId; // 角色主键ID

    /** 角色名称 */
    @Excel(name = "角色名称") // Excel导出配置
    private String roleName; // 角色名称

    /** 角色权限 */
    @Excel(name = "角色权限") // Excel导出配置
    private String roleKey; // 角色权限标识（如：admin）

    /** 角色排序 */
    @Excel(name = "角色排序") // Excel导出配置
    private Integer roleSort; // 显示排序

    /** 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限） */
    @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限") // Excel导出转换
    private String dataScope; // 数据权限范围

    /** 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示） */
    private boolean menuCheckStrictly; // 菜单树父子节点是否关联选中

    /** 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ） */
    private boolean deptCheckStrictly; // 部门树父子节点是否关联选中

    /** 角色状态（0正常 1停用） */
    @Excel(name = "角色状态", readConverterExp = "0=正常,1=停用") // Excel导出转换
    private String status; // 角色状态

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag; // 逻辑删除标志

    /** 用户是否存在此角色标识 默认不存在 */
    private boolean flag = false; // 前端显示标识，标记用户是否已拥有该角色

    /** 菜单组 */
    private Long[] menuIds; // 角色关联的菜单ID数组

    /** 部门组（数据权限） */
    private Long[] deptIds; // 角色关联的部门ID数组（数据权限用）

    /** 角色菜单权限 */
    private Set<String> permissions; // 角色拥有的权限标识集合

    public SysRole() // 无参构造方法
    {

    }

    public SysRole(Long roleId) // 根据ID构造
    {
        this.roleId = roleId; // 设置角色ID
    }

    public Long getRoleId() { return roleId; } // 获取角色ID

    public void setRoleId(Long roleId) { this.roleId = roleId; } // 设置角色ID

    public boolean isAdmin() // 判断当前角色是否为管理员
    {
        return isAdmin(this.roleId); // 调用静态方法判断
    }

    public static boolean isAdmin(Long roleId) // 静态方法：判断指定角色ID是否为管理员
    {
        return roleId != null && 1L == roleId; // 角色ID为1的是管理员
    }

    @NotBlank(message = "角色名称不能为空") // 校验不能为空
    @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符") // 校验长度
    public String getRoleName() { return roleName; } // 获取角色名称

    public void setRoleName(String roleName) { this.roleName = roleName; } // 设置角色名称

    @NotBlank(message = "权限字符不能为空") // 校验不能为空
    @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符") // 校验长度
    public String getRoleKey() { return roleKey; } // 获取权限标识

    public void setRoleKey(String roleKey) { this.roleKey = roleKey; } // 设置权限标识

    @NotNull(message = "显示顺序不能为空") // 校验不能为空
    public Integer getRoleSort() { return roleSort; } // 获取排序号

    public void setRoleSort(Integer roleSort) { this.roleSort = roleSort; } // 设置排序号

    public String getDataScope() { return dataScope; } // 获取数据范围

    public void setDataScope(String dataScope) { this.dataScope = dataScope; } // 设置数据范围

    public boolean isMenuCheckStrictly() { return menuCheckStrictly; } // 获取菜单关联选中状态

    public void setMenuCheckStrictly(boolean menuCheckStrictly) { this.menuCheckStrictly = menuCheckStrictly; } // 设置菜单关联选中状态

    public boolean isDeptCheckStrictly() { return deptCheckStrictly; } // 获取部门关联选中状态

    public void setDeptCheckStrictly(boolean deptCheckStrictly) { this.deptCheckStrictly = deptCheckStrictly; } // 设置部门关联选中状态

    public String getStatus() { return status; } // 获取状态

    public void setStatus(String status) { this.status = status; } // 设置状态

    public String getDelFlag() { return delFlag; } // 获取删除标志

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; } // 设置删除标志

    public boolean isFlag() { return flag; } // 获取前端标识

    public void setFlag(boolean flag) { this.flag = flag; } // 设置前端标识

    public Long[] getMenuIds() { return menuIds; } // 获取菜单ID数组

    public void setMenuIds(Long[] menuIds) { this.menuIds = menuIds; } // 设置菜单ID数组

    public Long[] getDeptIds() { return deptIds; } // 获取部门ID数组

    public void setDeptIds(Long[] deptIds) { this.deptIds = deptIds; } // 设置部门ID数组

    public Set<String> getPermissions() { return permissions; } // 获取权限集合

    public void setPermissions(Set<String> permissions) { this.permissions = permissions; } // 设置权限集合

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("roleId", getRoleId()) // 角色ID
            .append("roleName", getRoleName()) // 角色名称
            .append("roleKey", getRoleKey()) // 权限标识
            .append("roleSort", getRoleSort()) // 排序号
            .append("dataScope", getDataScope()) // 数据范围
            .append("menuCheckStrictly", isMenuCheckStrictly()) // 菜单关联选中
            .append("deptCheckStrictly", isDeptCheckStrictly()) // 部门关联选中
            .append("status", getStatus()) // 状态
            .append("delFlag", getDelFlag()) // 删除标志
            .append("createBy", getCreateBy()) // 创建者
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateBy", getUpdateBy()) // 更新者
            .append("updateTime", getUpdateTime()) // 更新时间
            .append("remark", getRemark()) // 备注
            .toString();
    }
}
