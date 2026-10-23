// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.util.List;
import com.debuff.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.trade.mapper.AnnouncementMapper;
import com.debuff.trade.domain.Announcement;
import com.debuff.trade.service.IAnnouncementService;

/**
 * 系统公告Service业务层处理
 * 
 * @author debuff
 * @date 2026-08-25
 */
@Service // 注册为Spring服务Bean
public class AnnouncementServiceImpl implements IAnnouncementService // 公告服务实现类
{
    @Autowired // 自动注入公告Mapper
    private AnnouncementMapper announcementMapper;

    /**
     * 查询系统公告
     * 
     * @param id 系统公告主键
     * @return 系统公告
     */
    @Override
    public Announcement selectAnnouncementById(Long id) // 根据ID查询公告
    {
        return announcementMapper.selectAnnouncementById(id); // 委托Mapper查询
    }

    /**
     * 查询系统公告列表
     * 
     * @param announcement 系统公告
     * @return 系统公告
     */
    @Override
    public List<Announcement> selectAnnouncementList(Announcement announcement) // 查询公告列表
    {
        return announcementMapper.selectAnnouncementList(announcement); // 委托Mapper查询列表
    }

    /**
     * 查询用户端可见的最新公告
     * 
     * @return 系统公告集合
     */
    @Override
    public List<Announcement> selectPublicAnnouncements() // 查询公开公告
    {
        return announcementMapper.selectPublicAnnouncements(); // 委托Mapper查询公开公告
    }

    /**
     * 新增系统公告
     * 
     * @param announcement 系统公告
     * @return 结果
     */
    @Override
    public int insertAnnouncement(Announcement announcement) // 新增公告
    {
        announcement.setCreateTime(DateUtils.getNowDate()); // 设置创建时间
        return announcementMapper.insertAnnouncement(announcement); // 插入数据库
    }

    /**
     * 修改系统公告
     * 
     * @param announcement 系统公告
     * @return 结果
     */
    @Override
    public int updateAnnouncement(Announcement announcement) // 修改公告
    {
        announcement.setUpdateTime(DateUtils.getNowDate()); // 设置更新时间
        return announcementMapper.updateAnnouncement(announcement); // 更新数据库
    }

    /**
     * 批量删除系统公告
     * 
     * @param ids 需要删除的系统公告主键
     * @return 结果
     */
    @Override
    public int deleteAnnouncementByIds(Long[] ids) // 批量删除公告
    {
        return announcementMapper.deleteAnnouncementByIds(ids); // 委托Mapper批量删除
    }

    /**
     * 删除系统公告信息
     * 
     * @param id 系统公告主键
     * @return 结果
     */
    @Override
    public int deleteAnnouncementById(Long id) // 删除单条公告
    {
        return announcementMapper.deleteAnnouncementById(id); // 委托Mapper删除
    }
}
