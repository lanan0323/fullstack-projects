// 包声明：系统实体类所在包
package com.debuff.common.core.domain.entity;

// 导入Date和List
import java.util.Date;
import java.util.List;
// 导入JSR303校验注解
import javax.validation.constraints.*;
// 导入Apache Commons的ToString构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入Excel导出注解
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
import com.debuff.common.annotation.Excel.Type;
// 导入多个Excel注解集合
import com.debuff.common.annotation.Excels;
// 导入实体基类
import com.debuff.common.core.domain.BaseEntity;
// 导入XSS校验注解
import com.debuff.common.xss.Xss;

/**
 * 用户对象 sys_user
 * 
 * @author ruoyi
 */
public class SysUser extends BaseEntity // 用户实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 用户ID */
    @Excel(name = "用户序号", cellType = ColumnType.NUMERIC, prompt = "用户编号") // Excel导出配置
    private Long userId; // 用户主键ID

    /** 部门ID */
    @Excel(name = "部门编号", type = Type.IMPORT) // 仅在导入时导出
    private Long deptId; // 所属部门ID

    /** 用户账号 */
    @Excel(name = "登录名称") // Excel导出配置
    private String userName; // 登录账号

    /** 用户昵称 */
    @Excel(name = "用户名称") // Excel导出配置
    private String nickName; // 用户昵称

    /** 用户邮箱 */
    @Excel(name = "用户邮箱") // Excel导出配置
    private String email; // 邮箱地址

    /** 手机号码 */
    @Excel(name = "手机号码") // Excel导出配置
    private String phonenumber; // 手机号

    /** 用户性别 */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知") // Excel导出时转换性别值
    private String sex; // 性别

    /** 用户头像 */
    private String avatar; // 头像路径

    /** 密码 */
    private String password; // 登录密码（加密存储）

    /** 帐号状态（0正常 1停用） */
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用") // Excel导出时转换
    private String status; // 账号状态

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag; // 逻辑删除标志

    /** 最后登录IP */
    @Excel(name = "最后登录IP", type = Type.EXPORT) // 仅导出时显示
    private String loginIp; // 最后登录IP地址

    /** 最后登录时间 */
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT) // 仅导出时显示
    private Date loginDate; // 最后登录时间

    /** 部门对象 */
    @Excels({ // 多个Excel列映射到部门对象的属性
        @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT),
        @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    private SysDept dept; // 关联的部门对象

    /** 角色对象 */
    private List<SysRole> roles; // 用户拥有的角色列表

    /** 角色组 */
    private Long[] roleIds; // 角色ID数组，用于新增/编辑时传递

    /** 岗位组 */
    private Long[] postIds; // 岗位ID数组，用于新增/编辑时传递

    /** 角色ID */
    private Long roleId; // 当前角色ID

    public SysUser() // 无参构造方法
    {

    }

    public SysUser(Long userId) // 根据用户ID构造
    {
        this.userId = userId; // 设置用户ID
    }

    public Long getUserId() { return userId; } // 获取用户ID

    public void setUserId(Long userId) { this.userId = userId; } // 设置用户ID

    public boolean isAdmin() // 判断当前用户是否为管理员
    {
        return isAdmin(this.userId); // 调用静态方法判断
    }

    public static boolean isAdmin(Long userId) // 静态方法：判断指定用户ID是否为管理员
    {
        return userId != null && 1L == userId; // 用户ID为1的是管理员
    }

    public Long getDeptId() { return deptId; } // 获取部门ID

    public void setDeptId(Long deptId) { this.deptId = deptId; } // 设置部门ID

    @Xss(message = "用户昵称不能包含脚本字符") // XSS校验
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符") // 校验长度
    public String getNickName() { return nickName; } // 获取昵称

    public void setNickName(String nickName) { this.nickName = nickName; } // 设置昵称

    @Xss(message = "用户账号不能包含脚本字符") // XSS校验
    @NotBlank(message = "用户账号不能为空") // 校验不能为空
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符") // 校验长度
    public String getUserName() { return userName; } // 获取用户名

    public void setUserName(String userName) { this.userName = userName; } // 设置用户名

    @Email(message = "邮箱格式不正确") // 校验邮箱格式
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符") // 校验长度
    public String getEmail() { return email; } // 获取邮箱

    public void setEmail(String email) { this.email = email; } // 设置邮箱

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符") // 校验长度
    public String getPhonenumber() { return phonenumber; } // 获取手机号

    public void setPhonenumber(String phonenumber) { this.phonenumber = phonenumber; } // 设置手机号

    public String getSex() { return sex; } // 获取性别

    public void setSex(String sex) { this.sex = sex; } // 设置性别

    public String getAvatar() { return avatar; } // 获取头像路径

    public void setAvatar(String avatar) { this.avatar = avatar; } // 设置头像路径

    public String getPassword() { return password; } // 获取密码

    public void setPassword(String password) { this.password = password; } // 设置密码

    public String getStatus() { return status; } // 获取状态

    public void setStatus(String status) { this.status = status; } // 设置状态

    public String getDelFlag() { return delFlag; } // 获取删除标志

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; } // 设置删除标志

    public String getLoginIp() { return loginIp; } // 获取登录IP

    public void setLoginIp(String loginIp) { this.loginIp = loginIp; } // 设置登录IP

    public Date getLoginDate() { return loginDate; } // 获取登录时间

    public void setLoginDate(Date loginDate) { this.loginDate = loginDate; } // 设置登录时间

    public SysDept getDept() { return dept; } // 获取部门对象

    public void setDept(SysDept dept) { this.dept = dept; } // 设置部门对象

    public List<SysRole> getRoles() { return roles; } // 获取角色列表

    public void setRoles(List<SysRole> roles) { this.roles = roles; } // 设置角色列表

    public Long[] getRoleIds() { return roleIds; } // 获取角色ID数组

    public void setRoleIds(Long[] roleIds) { this.roleIds = roleIds; } // 设置角色ID数组

    public Long[] getPostIds() { return postIds; } // 获取岗位ID数组

    public void setPostIds(Long[] postIds) { this.postIds = postIds; } // 设置岗位ID数组

    public Long getRoleId() { return roleId; } // 获取角色ID

    public void setRoleId(Long roleId) { this.roleId = roleId; } // 设置角色ID

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId()) // 用户ID
            .append("deptId", getDeptId()) // 部门ID
            .append("userName", getUserName()) // 用户名
            .append("nickName", getNickName()) // 昵称
            .append("email", getEmail()) // 邮箱
            .append("phonenumber", getPhonenumber()) // 手机号
            .append("sex", getSex()) // 性别
            .append("avatar", getAvatar()) // 头像
            .append("password", getPassword()) // 密码
            .append("status", getStatus()) // 状态
            .append("delFlag", getDelFlag()) // 删除标志
            .append("loginIp", getLoginIp()) // 登录IP
            .append("loginDate", getLoginDate()) // 登录时间
            .append("createBy", getCreateBy()) // 创建者
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateBy", getUpdateBy()) // 更新者
            .append("updateTime", getUpdateTime()) // 更新时间
            .append("remark", getRemark()) // 备注
            .append("dept", getDept()) // 部门对象
            .toString();
    }
}
