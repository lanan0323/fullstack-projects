// 包声明
package com.debuff.system.service;

// 导入集合类和通知公告实体类
import java.util.List;
import com.debuff.system.domain.SysNotice;

/**
 * 公告 服务层
 * 
 * @author ruoyi
 */
// 通知公告服务接口，定义公告的增删改查方法
public interface ISysNoticeService
{
    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    // 根据ID查询单条公告详情
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    // 根据条件分页查询公告列表
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    // 新增一条公告记录
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    // 修改一条公告记录
    public int updateNotice(SysNotice notice);

    /**
     * 删除公告信息
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    // 根据ID删除单条公告
    public int deleteNoticeById(Long noticeId);
    
    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    // 批量删除多条公告
    public int deleteNoticeByIds(Long[] noticeIds);
}
