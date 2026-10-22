// 包声明
package com.debuff.system.service.impl;

// 导入相关依赖
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.common.constant.UserConstants;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.StringUtils;
import com.debuff.system.domain.SysPost;
import com.debuff.system.mapper.SysPostMapper;
import com.debuff.system.mapper.SysUserPostMapper;
import com.debuff.system.service.ISysPostService;

/**
 * 岗位信息 服务层处理
 * 
 * @author ruoyi
 */
@Service // 注册为Spring服务Bean
public class SysPostServiceImpl implements ISysPostService
{
    @Autowired // 自动注入岗位Mapper
    private SysPostMapper postMapper;

    @Autowired // 自动注入用户岗位关联Mapper
    private SysUserPostMapper userPostMapper;

    /**
     * 查询岗位信息集合
     * 
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post)
    {
        return postMapper.selectPostList(post); // 条件查询岗位列表
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        return postMapper.selectPostAll(); // 查询全部岗位
    }

    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId)
    {
        return postMapper.selectPostById(postId); // 根据ID查询岗位详情
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId)
    {
        return postMapper.selectPostListByUserId(userId); // 查询用户关联的岗位ID列表
    }

    /**
     * 校验岗位名称是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId(); // 获取岗位ID，新增时为-1
        SysPost info = postMapper.checkPostNameUnique(post.getPostName()); // 根据名称查询岗位
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE; // 名称已存在，不唯一
        }
        return UserConstants.UNIQUE; // 名称唯一
    }

    /**
     * 校验岗位编码是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId(); // 获取岗位ID，新增时为-1
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode()); // 根据编码查询岗位
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE; // 编码已存在，不唯一
        }
        return UserConstants.UNIQUE; // 编码唯一
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId)
    {
        return userPostMapper.countUserPostById(postId); // 统计该岗位关联的用户数量
    }

    /**
     * 删除岗位信息
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId)
    {
        return postMapper.deletePostById(postId); // 根据ID删除岗位
    }

    /**
     * 批量删除岗位信息
     * 
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds)
    {
        for (Long postId : postIds) // 遍历每个待删除的岗位ID
        {
            SysPost post = selectPostById(postId); // 查询岗位信息
            if (countUserPostById(postId) > 0) // 检查是否有用户关联该岗位
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName())); // 有用户关联则禁止删除
            }
        }
        return postMapper.deletePostByIds(postIds); // 批量删除岗位
    }

    /**
     * 新增保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post)
    {
        return postMapper.insertPost(post); // 插入新岗位
    }

    /**
     * 修改保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post)
    {
        return postMapper.updatePost(post); // 更新岗位信息
    }
}
