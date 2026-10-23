// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Java验证注解库（非空校验、长度校验等）
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
// 导入Apache Commons的字符串构建器，用于生成对象的字符串表示
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入RuoYi框架的通用实体基类
import com.debuff.common.core.domain.BaseEntity;
// 导入XSS防注入校验注解
import com.debuff.common.xss.Xss;

/**
 * 通知公告表 sys_notice
 * 
 * @author ruoyi
 */
public class SysNotice extends BaseEntity // 继承BaseEntity，获得创建人、创建时间等公共字段
{
    private static final long serialVersionUID = 1L; // 序列化版本号，保证反序列化兼容性

    /** 公告ID */
    private Long noticeId;

    /** 公告标题 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;

    public Long getNoticeId() // 获取公告ID
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) // 设置公告ID
    {
        this.noticeId = noticeId;
    }

    public void setNoticeTitle(String noticeTitle) // 设置公告标题
    {
        this.noticeTitle = noticeTitle;
    }

    @Xss(message = "公告标题不能包含脚本字符") // XSS校验：防止脚本注入
    @NotBlank(message = "公告标题不能为空") // 非空校验
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符") // 长度校验：最多50个字符
    public String getNoticeTitle() // 获取公告标题
    {
        return noticeTitle;
    }

    public void setNoticeType(String noticeType) // 设置公告类型
    {
        this.noticeType = noticeType;
    }

    public String getNoticeType() // 获取公告类型
    {
        return noticeType;
    }

    public void setNoticeContent(String noticeContent) // 设置公告内容
    {
        this.noticeContent = noticeContent;
    }

    public String getNoticeContent() // 获取公告内容
    {
        return noticeContent;
    }

    public void setStatus(String status) // 设置公告状态
    {
        this.status = status;
    }

    public String getStatus() // 获取公告状态
    {
        return status;
    }

    @Override
    public String toString() { // 重写toString方法，输出多行格式的对象信息
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("noticeId", getNoticeId()) // 追加公告ID
            .append("noticeTitle", getNoticeTitle()) // 追加公告标题
            .append("noticeType", getNoticeType()) // 追加公告类型
            .append("noticeContent", getNoticeContent()) // 追加公告内容
            .append("status", getStatus()) // 追加公告状态
            .append("createBy", getCreateBy()) // 追加创建者
            .append("createTime", getCreateTime()) // 追加创建时间
            .append("updateBy", getUpdateBy()) // 追加更新者
            .append("updateTime", getUpdateTime()) // 追加更新时间
            .append("remark", getRemark()) // 追加备注
            .toString(); // 转换为字符串
    }
}
