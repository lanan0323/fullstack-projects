// 包声明
package com.debuff.trade.domain;

// 导入所需类
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 用户端首页轮播图对象 debuff_banner
 */
public class Banner extends BaseEntity
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    private Long id; // 轮播图ID
    private String title; // 标题
    private String subtitle; // 副标题
    private String imageUrl; // 图片URL
    private String linkUrl; // 跳转链接
    private Integer sortOrder; // 排序号
    private String status; // 状态

    /** 获取轮播图ID */
    public Long getId() { return id; }
    /** 设置轮播图ID */
    public void setId(Long id) { this.id = id; }
    /** 获取标题 */
    public String getTitle() { return title; }
    /** 设置标题 */
    public void setTitle(String title) { this.title = title; }
    /** 获取副标题 */
    public String getSubtitle() { return subtitle; }
    /** 设置副标题 */
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    /** 获取图片URL */
    public String getImageUrl() { return imageUrl; }
    /** 设置图片URL */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    /** 获取跳转链接 */
    public String getLinkUrl() { return linkUrl; }
    /** 设置跳转链接 */
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
    /** 获取排序号 */
    public Integer getSortOrder() { return sortOrder; }
    /** 设置排序号 */
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    /** 获取状态 */
    public String getStatus() { return status; }
    /** 设置状态 */
    public void setStatus(String status) { this.status = status; }

    /** 转字符串表示 */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE) // 多行格式
            .append("id", getId()) // 追加ID
            .append("title", getTitle()) // 追加标题
            .append("subtitle", getSubtitle()) // 追加副标题
            .append("imageUrl", getImageUrl()) // 追加图片URL
            .append("linkUrl", getLinkUrl()) // 追加跳转链接
            .append("sortOrder", getSortOrder()) // 追加排序号
            .append("status", getStatus()) // 追加状态
            .append("createBy", getCreateBy()) // 追加创建者
            .append("createTime", getCreateTime()) // 追加创建时间
            .append("updateBy", getUpdateBy()) // 追加更新者
            .append("updateTime", getUpdateTime()) // 追加更新时间
            .append("remark", getRemark()) // 追加备注
            .toString(); // 转为字符串
    }
}
