// 包声明：核心域对象所在包
package com.debuff.common.core.domain;

// 导入ArrayList用于存储子节点列表
import java.util.ArrayList;
// 导入List接口
import java.util.List;

/**
 * Tree基类
 * 
 * @author ruoyi
 */
public class TreeEntity extends BaseEntity // 树形实体基类，继承BaseEntity
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 父菜单名称 */
    private String parentName; // 父节点名称

    /** 父菜单ID */
    private Long parentId; // 父节点ID

    /** 显示顺序 */
    private Integer orderNum; // 排序序号

    /** 祖级列表 */
    private String ancestors; // 祖级列表（如：0,100,200）

    /** 子部门 */
    private List<?> children = new ArrayList<>(); // 子节点列表

    public String getParentName() { return parentName; } // 获取父节点名称

    public void setParentName(String parentName) { this.parentName = parentName; } // 设置父节点名称

    public Long getParentId() { return parentId; } // 获取父节点ID

    public void setParentId(Long parentId) { this.parentId = parentId; } // 设置父节点ID

    public Integer getOrderNum() { return orderNum; } // 获取排序序号

    public void setOrderNum(Integer orderNum) { this.orderNum = orderNum; } // 设置排序序号

    public String getAncestors() { return ancestors; } // 获取祖级列表

    public void setAncestors(String ancestors) { this.ancestors = ancestors; } // 设置祖级列表

    public List<?> getChildren() { return children; } // 获取子节点列表

    public void setChildren(List<?> children) { this.children = children; } // 设置子节点列表
}
