// 包声明：核心域对象所在包
package com.debuff.common.core.domain;

// 导入Serializable接口
import java.io.Serializable;
// 导入List接口
import java.util.List;
// 导入Collectors用于流式收集
import java.util.stream.Collectors;
// 导入JsonInclude注解，控制JSON序列化时忽略空值
import com.fasterxml.jackson.annotation.JsonInclude;
// 导入部门实体类
import com.debuff.common.core.domain.entity.SysDept;
// 导入菜单实体类
import com.debuff.common.core.domain.entity.SysMenu;

/**
 * Treeselect树结构实体类
 * 
 * @author ruoyi
 */
public class TreeSelect implements Serializable // 树选择器实体，用于前端下拉树组件
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 节点ID */
    private Long id; // 节点ID

    /** 节点名称 */
    private String label; // 节点显示名称

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 子节点为空时不序列化到JSON
    private List<TreeSelect> children; // 子节点列表

    public TreeSelect() // 无参构造方法
    {

    }

    public TreeSelect(SysDept dept) // 从部门对象构造树节点
    {
        this.id = dept.getDeptId(); // 设置节点ID为部门ID
        this.label = dept.getDeptName(); // 设置节点名称为部门名称
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList()); // 递归构造子节点
    }

    public TreeSelect(SysMenu menu) // 从菜单对象构造树节点
    {
        this.id = menu.getMenuId(); // 设置节点ID为菜单ID
        this.label = menu.getMenuName(); // 设置节点名称为菜单名称
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList()); // 递归构造子节点
    }

    public Long getId() { return id; } // 获取节点ID

    public void setId(Long id) { this.id = id; } // 设置节点ID

    public String getLabel() { return label; } // 获取节点名称

    public void setLabel(String label) { this.label = label; } // 设置节点名称

    public List<TreeSelect> getChildren() { return children; } // 获取子节点列表

    public void setChildren(List<TreeSelect> children) { this.children = children; } // 设置子节点列表
}
