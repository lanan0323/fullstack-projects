// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.util.List;
import com.debuff.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.trade.mapper.FavoriteMapper;
import com.debuff.trade.domain.Favorite;
import com.debuff.trade.domain.vo.FavoriteVO;
import com.debuff.trade.service.IFavoriteService;

/**
 * 收藏Service业务层处理
 * 
 * @author debuff
 * @date 2026-08-26
 */
@Service // 注册为Spring服务Bean
public class FavoriteServiceImpl implements IFavoriteService // 收藏服务实现类
{
    @Autowired // 自动注入收藏Mapper
    private FavoriteMapper favoriteMapper;

    /**
     * 查询收藏
     * 
     * @param id 收藏主键
     * @return 收藏
     */
    @Override
    public Favorite selectFavoriteById(Long id) // 根据ID查询收藏
    {
        return favoriteMapper.selectFavoriteById(id); // 委托Mapper查询
    }

    /**
     * 查询收藏列表
     * 
     * @param favorite 收藏
     * @return 收藏
     */
    @Override
    public List<Favorite> selectFavoriteList(Favorite favorite) // 查询收藏列表
    {
        return favoriteMapper.selectFavoriteList(favorite); // 委托Mapper查询列表
    }

    /**
     * 新增收藏
     * 
     * @param favorite 收藏
     * @return 结果
     */
    @Override
    public int insertFavorite(Favorite favorite) // 新增收藏
    {
        favorite.setCreateTime(DateUtils.getNowDate()); // 设置创建时间
        return favoriteMapper.insertFavorite(favorite); // 插入数据库
    }

    /**
     * 修改收藏
     * 
     * @param favorite 收藏
     * @return 结果
     */
    @Override
    public int updateFavorite(Favorite favorite) // 修改收藏
    {
        return favoriteMapper.updateFavorite(favorite); // 委托Mapper更新
    }

    /**
     * 批量删除收藏
     * 
     * @param ids 需要删除的收藏主键
     * @return 结果
     */
    @Override
    public int deleteFavoriteByIds(Long[] ids) // 批量删除收藏
    {
        return favoriteMapper.deleteFavoriteByIds(ids); // 委托Mapper批量删除
    }

    /**
     * 删除收藏信息
     * 
     * @param id 收藏主键
     * @return 结果
     */
    @Override
    public int deleteFavoriteById(Long id) // 删除单条收藏
    {
        return favoriteMapper.deleteFavoriteById(id); // 委托Mapper删除
    }

    @Override
    public List<FavoriteVO> selectUserFavoriteList(Long userId) // 查询用户收藏列表
    {
        return favoriteMapper.selectUserFavoriteList(userId); // 委托Mapper查询
    }
}
