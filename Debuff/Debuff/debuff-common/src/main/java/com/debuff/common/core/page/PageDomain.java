// 包声明：分页数据所在包
package com.debuff.common.core.page;

// 导入字符串工具类
import com.debuff.common.utils.StringUtils;

/**
 * 分页数据
 * 
 * @author ruoyi
 */
public class PageDomain // 分页参数封装
{
    /** 当前记录起始索引 */
    private Integer pageNum; // 当前页码

    /** 每页显示记录数 */
    private Integer pageSize; // 每页大小

    /** 排序列 */
    private String orderByColumn; // 排序字段名

    /** 排序的方向desc或者asc */
    private String isAsc = "asc"; // 排序方向，默认升序

    /** 分页参数合理化 */
    private Boolean reasonable = true; // 分页合理化开关

    public String getOrderBy() // 获取排序SQL片段
    {
        if (StringUtils.isEmpty(orderByColumn)) // 排序字段为空时返回空字符串
        {
            return "";
        }
        return StringUtils.toUnderScoreCase(orderByColumn) + " " + isAsc; // 驼峰转下划线并拼接排序方向
    }

    public Integer getPageNum() { return pageNum; } // 获取页码

    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; } // 设置页码

    public Integer getPageSize() { return pageSize; } // 获取每页大小

    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; } // 设置每页大小

    public String getOrderByColumn() { return orderByColumn; } // 获取排序字段

    public void setOrderByColumn(String orderByColumn) { this.orderByColumn = orderByColumn; } // 设置排序字段

    public String getIsAsc() { return isAsc; } // 获取排序方向

    public void setIsAsc(String isAsc) // 设置排序方向
    {
        if (StringUtils.isNotEmpty(isAsc)) // 排序方向不为空时处理
        {
            // 兼容前端排序类型
            if ("ascending".equals(isAsc)) // 前端ascending转为asc
            {
                isAsc = "asc";
            }
            else if ("descending".equals(isAsc)) // 前端descending转为desc
            {
                isAsc = "desc";
            }
            this.isAsc = isAsc; // 设置排序方向
        }
    }

    public Boolean getReasonable() // 获取合理化开关
    {
        if (StringUtils.isNull(reasonable)) // 为空时默认返回true
        {
            return Boolean.TRUE;
        }
        return reasonable; // 返回当前值
    }

    public void setReasonable(Boolean reasonable) { this.reasonable = reasonable; } // 设置合理化开关
}
