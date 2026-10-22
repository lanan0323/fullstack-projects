// 包声明
package com.debuff.system.mapper;

// 导入集合类和用户岗位关联实体类
import java.util.List;
import com.debuff.system.domain.SysUserPost;

/**
 * 用户与岗位关联表 数据层
 * 
 * @author ruoyi
 */
// 用户岗位关联Mapper接口，定义用户与岗位关联关系的增删查方法
public interface SysUserPostMapper
{
    /**
     * 通过用户ID删除用户和岗位关联
     * 
     * @param userId 用户ID
     * @return 结果
     */
    // 根据用户ID删除该用户的所有岗位关联
    public int deleteUserPostByUserId(Long userId);

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    // 统计指定岗位关联了多少用户
    public int countUserPostById(Long postId);

    /**
     * 批量删除用户和岗位关联
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    // 批量删除用户岗位关联记录
    public int deleteUserPost(Long[] ids);

    /**
     * 批量新增用户岗位信息
     * 
     * @param userPostList 用户岗位列表
     * @return 结果
     */
    // 批量插入用户与岗位的关联记录
    public int batchUserPost(List<SysUserPost> userPostList);
}
