// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.Favorite;
import com.debuff.trade.domain.vo.FavoriteVO;

/**
 * 收藏Service接口
 * 
 * @author debuff
 * @date 2026-08-26
 */
public interface IFavoriteService // 收藏服务接口
{
    /**
     * 查询收藏
     * 
     * @param id 收藏主键
     * @return 收藏
     */
    public Favorite selectFavoriteById(Long id); // 根据ID查询收藏

    /**
     * 查询收藏列表
     * 
     * @param favorite 收藏
     * @return 收藏集合
     */
    public List<Favorite> selectFavoriteList(Favorite favorite); // 查询收藏列表

    /**
     * 新增收藏
     * 
     * @param favorite 收藏
     * @return 结果
     */
    public int insertFavorite(Favorite favorite); // 新增收藏

    /**
     * 修改收藏
     * 
     * @param favorite 收藏
     * @return 结果
     */
    public int updateFavorite(Favorite favorite); // 修改收藏

    /**
     * 批量删除收藏
     * 
     * @param ids 需要删除的收藏主键集合
     * @return 结果
     */
    public int deleteFavoriteByIds(Long[] ids); // 批量删除收藏

    /**
     * 删除收藏信息
     * 
     * @param id 收藏主键
     * @return 结果
     */
    public int deleteFavoriteById(Long id); // 删除单条收藏

    /**
     * 查询用户收藏列表（含饰品和挂单信息）
     * 
     * @param userId 用户ID
     * @return 收藏VO集合
     */
    public List<FavoriteVO> selectUserFavoriteList(Long userId); // 查询用户收藏列表
}
