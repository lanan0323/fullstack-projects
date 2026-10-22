// 包声明
package com.debuff.trade.domain;

// 导入所需类
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.debuff.common.annotation.Excel;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 系统公告对象 debuff_announcement
 * 
 * @author debuff
 * @date 2026-08-25
 */
public class Announcement extends BaseEntity // 系统公告实体类，继承基础实体
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 自增主键 */
    private Long id;

    /** 公告标题 */
    @Excel(name = "公告标题") // Excel导出列名
    private String title;

    /** 公告内容 */
    @Excel(name = "公告内容") // Excel导出列名
    private String content;

    /** 公告类型 */
    @Excel(name = "公告类型") // Excel导出列名
    private String announcementType;

    /** 状态 */
    @Excel(name = "状态") // Excel导出列名
    private String status;

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
    /** 设置公告标题 */
    public void setTitle(String title) 
    {
        this.title = title;
    }

    /** 获取公告标题 */
    public String getTitle() 
    {
        return title;
    }
    /** 设置公告内容 */
    public void setContent(String content) 
    {
        this.content = content;
    }

    /** 获取公告内容 */
    public String getContent() 
    {
        return content;
    }
    /** 设置公告类型 */
    public void setAnnouncementType(String announcementType) 
    {
        this.announcementType = announcementType;
    }

    /** 获取公告类型 */
    public String getAnnouncementType() 
    {
        return announcementType;
    }
    /** 设置状态 */
    public void setStatus(String status) 
    {
        this.status = status;
    }

    /** 获取状态 */
    public String getStatus() 
    {
        return status;
    }

    @Override // 重写toString方法，用于调试输出
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("content", getContent())
            .append("announcementType", getAnnouncementType())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
