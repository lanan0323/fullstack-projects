// 包声明：系统实体类所在包
package com.debuff.common.core.domain.entity;

// 导入ArrayList和List用于子部门列表
import java.util.ArrayList;
import java.util.List;
// 导入JSR303校验注解
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
// 导入Apache Commons的ToString构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入实体基类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 部门表 sys_dept
 * 
 * @author ruoyi
 */
public class SysDept extends BaseEntity // 部门实体类，继承BaseEntity基类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 部门ID */
    private Long deptId; // 部门主键ID

    /** 父部门ID */
    private Long parentId; // 父部门ID，用于构建层级关系

    /** 祖级列表 */
    private String ancestors; // 祖级列表（如：0,100,200），用于快速查询所有祖先

    /** 部门名称 */
    private String deptName; // 部门名称

    /** 显示顺序 */
    private Integer orderNum; // 显示排序

    /** 负责人 */
    private String leader; // 部门负责人姓名

    /** 联系电话 */
    private String phone; // 联系电话

    /** 邮箱 */
    private String email; // 联系邮箱

    /** 部门状态:0正常,1停用 */
    private String status; // 部门状态

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag; // 逻辑删除标志

    /** 父部门名称 */
    private String parentName; // 父部门名称，非持久化字段

    /** 子部门 */
    private List<SysDept> children = new ArrayList<SysDept>(); // 子部门列表

    public Long getDeptId() { return deptId; } // 获取部门ID

    public void setDeptId(Long deptId) { this.deptId = deptId; } // 设置部门ID

    public Long getParentId() { return parentId; } // 获取父部门ID

    public void setParentId(Long parentId) { this.parentId = parentId; } // 设置父部门ID

    public String getAncestors() { return ancestors; } // 获取祖级列表

    public void setAncestors(String ancestors) { this.ancestors = ancestors; } // 设置祖级列表

    @NotBlank(message = "部门名称不能为空") // 校验部门名称不能为空
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符") // 校验长度限制
    public String getDeptName() { return deptName; } // 获取部门名称

    public void setDeptName(String deptName) { this.deptName = deptName; } // 设置部门名称

    @NotNull(message = "显示顺序不能为空") // 校验排序不能为空
    public Integer getOrderNum() { return orderNum; } // 获取排序号

    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; } // 设置排序号

    public String getLeader() { return leader; } // 获取负责人

    public void setLeader(String leader) { this.leader = leader; } // 设置负责人

    @Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符") // 校验手机号长度
    public String getPhone() { return phone; } // 获取联系电话

    public void setPhone(String phone) { this.phone = phone; } // 设置联系电话

    @Email(message = "邮箱格式不正确") // 校验邮箱格式
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符") // 校验邮箱长度
    public String getEmail() { return email; } // 获取邮箱

    public void setEmail(String email) { this.email = email; } // 设置邮箱

    public String getStatus() { return status; } // 获取状态

    public void setStatus(String status) { this.status = status; } // 设置状态

    public String getDelFlag() { return delFlag; } // 获取删除标志

    public void setDelFlag(String delFlag) { this.delFlag = delFlag; } // 设置删除标志

    public String getParentName() { return parentName; } // 获取父部门名称

    public void setParentName(String parentName) { this.parentName = parentName; } // 设置父部门名称

    public List<SysDept> getChildren() { return children; } // 获取子部门列表

    public void setChildren(List<SysDept> children) { this.children = children; } // 设置子部门列表

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE) // 使用多行格式
            .append("deptId", getDeptId()) // 部门ID
            .append("parentId", getParentId()) // 父部门ID
            .append("ancestors", getAncestors()) // 祖级列表
            .append("deptName", getDeptName()) // 部门名称
            .append("orderNum", getOrderNum()) // 排序号
            .append("leader", getLeader()) // 负责人
            .append("phone", getPhone()) // 联系电话
            .append("email", getEmail()) // 邮箱
            .append("status", getStatus()) // 状态
            .append("delFlag", getDelFlag()) // 删除标志
            .append("createBy", getCreateBy()) // 创建者
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateBy", getUpdateBy()) // 更新者
            .append("updateTime", getUpdateTime()) // 更新时间
            .toString(); // 转为字符串
    }
}
