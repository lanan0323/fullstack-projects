// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Apache Commons的字符串构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户和岗位关联 sys_user_post
 * 
 * @author ruoyi
 */
public class SysUserPost // 用户与岗位的关联关系实体类
{
    /** 用户ID */
    private Long userId;
    
    /** 岗位ID */
    private Long postId;

    public Long getUserId() // 获取用户ID
    {
        return userId;
    }

    public void setUserId(Long userId) // 设置用户ID
    {
        this.userId = userId;
    }

    public Long getPostId() // 获取岗位ID
    {
        return postId;
    }

    public void setPostId(Long postId) // 设置岗位ID
    {
        this.postId = postId;
    }

    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId()) // 追加用户ID
            .append("postId", getPostId()) // 追加岗位ID
            .toString();
    }
}
