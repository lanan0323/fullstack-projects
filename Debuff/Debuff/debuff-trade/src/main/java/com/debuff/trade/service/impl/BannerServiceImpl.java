// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.common.utils.DateUtils;
import com.debuff.trade.domain.Banner;
import com.debuff.trade.mapper.BannerMapper;
import com.debuff.trade.service.IBannerService;

/** 首页轮播图服务层实现 */
@Service
public class BannerServiceImpl implements IBannerService
{
    @Autowired
    private BannerMapper bannerMapper; // 轮播图Mapper

    /** 根据ID查询轮播图 */
    @Override
    public Banner selectBannerById(Long id) { return bannerMapper.selectBannerById(id); }

    /** 查询轮播图列表 */
    @Override
    public List<Banner> selectBannerList(Banner banner) { return bannerMapper.selectBannerList(banner); }

    /** 查询用户端已发布轮播图 */
    @Override
    public List<Banner> selectPublicBanners() { return bannerMapper.selectPublicBanners(); }

    /** 新增轮播图，自动设置创建时间 */
    @Override
    public int insertBanner(Banner banner)
    {
        banner.setCreateTime(DateUtils.getNowDate()); // 设置创建时间为当前时间
        return bannerMapper.insertBanner(banner); // 插入轮播图
    }

    /** 修改轮播图，自动设置更新时间 */
    @Override
    public int updateBanner(Banner banner)
    {
        banner.setUpdateTime(DateUtils.getNowDate()); // 设置更新时间为当前时间
        return bannerMapper.updateBanner(banner); // 更新轮播图
    }

    /** 批量删除轮播图 */
    @Override
    public int deleteBannerByIds(Long[] ids) { return bannerMapper.deleteBannerByIds(ids); }

    /** 删除轮播图 */
    @Override
    public int deleteBannerById(Long id) { return bannerMapper.deleteBannerById(id); }
}
