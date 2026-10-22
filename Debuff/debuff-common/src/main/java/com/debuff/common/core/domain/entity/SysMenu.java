// 包声明：系统实体类所在包
package com.debuff.common.core.domain.entity;

// 导入ArrayList和List用于子菜单列表
import java.util.ArrayList;
import java.util.List;
// 导入JSR303校验注解
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
// 导入Apache Commons的ToString构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入实体基类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 菜单权限表 sys_menu
 * 
 * @author ruoyi
 */
public class SysMenu extends BaseEntity // 菜单权限实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 菜单ID */
    private Long menuId; // 菜单主键ID

    /** 菜单名称 */
    private String menuName; // 菜单显示名称

    /** 父菜单名称 */
    private String parentName; // 父菜单名称，非持久化字段

    /** 父菜单ID */
    private Long parentId; // 父菜单ID

    /** 显示顺序 */
    private Integer orderNum; // 排序序号

    /** 路由地址 */
    private String path; // 前端路由路径

    /** 组件路径 */
    private String component; // 前端组件路径

    /** 路由参数 */
    private String query; // 路由参数

    /** 是否为外链（0是 1否） */
    private String isFrame; // 是否外链标识

    /** 是否缓存（0缓存 1不缓存） */
    private String isCache; // 是否缓存标识

    /** 类型（M目录 C菜单 F按钮） */
    private String menuType; // 菜单类型：M目录/C菜单/F按钮

    /** 显示状态（0显示 1隐藏） */
    private String visible; // 是否可见

    /** 菜单状态（0正常 1停用） */
    private String status; // 菜单状态

    /** 权限字符串 */
    private String perms; // 权限标识（如：system:user:list）

    /** 菜单图标 */
    private String icon; // 菜单图标名称

    /** 子菜单 */
    private List<SysMenu> children = new ArrayList<SysMenu>(); // 子菜单列表

    public Long getMenuId() { return menuId; } // 获取菜单ID

    public void setMenuId(Long menuId) { this.menuId = menuId; } // 设置菜单ID

    @NotBlank(message = "菜单名称不能为空") // 校验名称不能为空
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符") // 校验长度
    public String getMenuName() { return menuName; } // 获取菜单名称

    public void setMenuName(String menuName) { this.menuName = menuName; } // 设置菜单名称

    public String getParentName() { return parentName; } // 获取父菜单名称

    public void setParentName(String parentName) { this.parentName = parentName; } // 设置父菜单名称

    public Long getParentId() { return parentId; } // 获取父菜单ID

    public void setParentId(Long parentId) { this.parentId = parentId; } // 设置父菜单ID

    @NotNull(message = "显示顺序不能为空") // 校验排序不能为空
    public Integer getOrderNum() { return orderNum; } // 获取排序号

    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; } // 设置排序号

    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符") // 校验长度
    public String getPath() { return path; } // 获取路由地址

    public void setPath(String path) { this.path = path; } // 设置路由地址

    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符") // 校验长度
    public String getComponent() { return component; } // 获取组件路径

    public void setComponent(String component) { this.component = component; } // 设置组件路径

    public String getQuery() { return query; } // 获取路由参数

    public void setQuery(String query) { this.query = query; } // 设置路由参数

    public String getIsFrame() { return isFrame; } // 获取是否外链

    public void setIsFrame(String isFrame) { this.isFrame = isFrame; } // 设置是否外链

    public String getIsCache() { return isCache; } // 获取是否缓存

    public void setIsCache(String isCache) { this.isCache = isCache; } // 设置是否缓存

    @NotBlank(message = "菜单类型不能为空") // 校验类型不能为空
    public String getMenuType() { return menuType; } // 获取菜单类型

    public void setMenuType(String menuType) { this.menuType = menuType; } // 设置菜单类型

    public String getVisible() { return visible; } // 获取显示状态

    public void setVisible(String visible) { this.visible = visible; } // 设置显示状态

    public String getStatus() { return status; } // 获取菜单状态

    public void setStatus(String status) { this.status = status; } // 设置菜单状态

    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符") // 校验长度
    public String getPerms() { return perms; } // 获取权限标识

    public void setPerms(String perms) { this.perms = perms; } // 设置权限标识

    public String getIcon() { return icon; } // 获取菜单图标

    public void setIcon(String icon) { this.icon = icon; } // 设置菜单图标

    public List<SysMenu> getChildren() { return children; } // 获取子菜单列表

    public void setChildren(List<SysMenu> children) { this.children = children; } // 设置子菜单列表
    
    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("menuId", getMenuId()) // 菜单ID
            .append("menuName", getMenuName()) // 菜单名称
            .append("parentId", getParentId()) // 父菜单ID
            .append("orderNum", getOrderNum()) // 排序号
            .append("path", getPath()) // 路由地址
            .append("component", getComponent()) // 组件路径
            .append("isFrame", getIsFrame()) // 是否外链
            .append("IsCache", getIsCache()) // 是否缓存
            .append("menuType", getMenuType()) // 菜单类型
            .append("visible", getVisible()) // 显示状态
            .append("status ", getStatus()) // 菜单状态
            .append("perms", getPerms()) // 权限标识
            .append("icon", getIcon()) // 菜单图标
            .append("createBy", getCreateBy()) // 创建者
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateBy", getUpdateBy()) // 更新者
            .append("updateTime", getUpdateTime()) // 更新时间
            .append("remark", getRemark()) // 备注
            .toString();
    }
}
