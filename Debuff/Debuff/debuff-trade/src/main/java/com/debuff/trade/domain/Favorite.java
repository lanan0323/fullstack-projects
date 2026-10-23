// 包声明
package com.debuff.trade.domain;

// 导入所需类
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.debuff.common.annotation.Excel;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 收藏对象 debuff_favorite
 * 
 * @author debuff
 * @date 2026-08-26
 */
public class Favorite extends BaseEntity // 收藏实体类，继承基础实体
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID") // Excel导出列名
    private Long userId;

    /** 在售饰品ID */
    @Excel(name = "在售饰品ID") // Excel导出列名
    private Long listingId;

    /** 设置主键ID */
    public void setId(Long id) 
    {
        this.id = id;
    }

    /** 获取主键ID */
    public Long getId() 
    {
        return id;
    }
    /** 设置用户ID */
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    /** 获取用户ID */
    public Long getUserId() 
    {
        return userId;
    }
    /** 设置在-in售饰品ID */
    public void setListingId(Long listingId) 
    {
        this.listingId = listingId;
    }

    /** 获取在售饰品ID */
    public Long getListingId() 
    {
        return listingId;
    }

    @Override // 重写toString方法，用于调试输出
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("listingId", getListingId())
            .append("createTime", getCreateTime())
            .toString();
    }
}
