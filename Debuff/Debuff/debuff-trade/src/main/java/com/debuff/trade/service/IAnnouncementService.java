// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.Announcement;

/**
 * 系统公告Service接口
 * 
 * @author debuff
 * @date 2026-08-25
 */
public interface IAnnouncementService // 公告服务接口
{
    /**
     * 查询系统公告
     * 
     * @param id 系统公告主键
     * @return 系统公告
     */
    public Announcement selectAnnouncementById(Long id); // 根据ID查询公告

    /**
     * 查询系统公告列表
     * 
     * @param announcement 系统公告
     * @return 系统公告集合
     */
    public List<Announcement> selectAnnouncementList(Announcement announcement); // 查询公告列表

    /**
     * 查询用户端可见的最新公告
     * 
     * @return 系统公告集合
     */
    public List<Announcement> selectPublicAnnouncements(); // 查询公开公告

    /**
     * 新增系统公告
     * 
     * @param announcement 系统公告
     * @return 结果
     */
    public int insertAnnouncement(Announcement announcement); // 新增公告

    /**
     * 修改系统公告
     * 
     * @param announcement 系统公告
     * @return 结果
     */
    public int updateAnnouncement(Announcement announcement); // 修改公告

    /**
     * 批量删除系统公告
     * 
     * @param ids 需要删除的系统公告主键集合
     * @return 结果
     */
    public int deleteAnnouncementByIds(Long[] ids); // 批量删除公告

    /**
     * 删除系统公告信息
     * 
     * @param id 系统公告主键
     * @return 结果
     */
    public int deleteAnnouncementById(Long id); // 删除单条公告
}
