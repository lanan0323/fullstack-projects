// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.Banner;

/** 首页轮播图服务层 */
public interface IBannerService
{
    /** 根据ID查询轮播图 */
    public Banner selectBannerById(Long id);

    /** 查询轮播图列表 */
    public List<Banner> selectBannerList(Banner banner);

    /** 查询用户端已发布轮播图 */
    public List<Banner> selectPublicBanners();

    /** 新增轮播图 */
    public int insertBanner(Banner banner);

    /** 修改轮播图 */
    public int updateBanner(Banner banner);

    /** 批量删除轮播图 */
    public int deleteBannerByIds(Long[] ids);

    /** 删除轮播图 */
    public int deleteBannerById(Long id);
}
