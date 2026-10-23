// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.entity.Skin;
import com.debuff.trade.domain.query.SkinMarketQuery;
import com.debuff.trade.domain.vo.SkinDetailVO;
import com.debuff.trade.domain.vo.SkinMarketVO;

/**
 * 饰品定义 数据层
 * 
 * @author B同学
 */
public interface SkinMapper // 饰品Mapper接口
{
    /**
     * 查询饰品列表（后台管理）
     * 
     * @param skin 饰品
     * @return 饰品集合
     */
    public List<Skin> selectSkinList(Skin skin); // 查询饰品列表

    /**
     * 通过ID查询饰品
     * 
     * @param skinId 饰品ID
     * @return 饰品
     */
    public Skin selectSkinById(Long skinId); // 根据ID查询饰品

    /**
     * 查询饰品市场列表（四表联查：饰品 + 武器类型 + 品质 + 在售）
     * 
     * @param query 查询条件
     * @return 市场列表VO集合
     */
    public List<SkinMarketVO> selectMarketList(SkinMarketQuery query); // 查询市场列表

    /**
     * 查询热门饰品（在售数量前N）
     * 
     * @param limit 条数
     * @return 市场列表VO集合
     */
    public List<SkinMarketVO> selectHotSkins(int limit); // 查询热门饰品

    /**
     * 查询饰品的在售列表
     * 
     * @param skinId 饰品ID
     * @return 在售条目集合
     */
    public List<SkinDetailVO.ListingItem> selectListingsBySkinId(Long skinId); // 查询在售列表

    /**
     * 查询饰品最近30天价格历史
     * 
     * @param skinId 饰品ID
     * @return 价格历史集合
     */
    public List<SkinDetailVO.PricePoint> selectPriceHistoryBySkinId(Long skinId); // 查询价格历史

    /**
     * 新增饰品
     * 
     * @param skin 饰品
     * @return 结果
     */
    public int insertSkin(Skin skin); // 新增饰品

    /**
     * 修改饰品
     * 
     * @param skin 饰品
     * @return 结果
     */
    public int updateSkin(Skin skin); // 修改饰品

    /**
     * 修改饰品状态（上架/下架）
     * 
     * @param skin 饰品
     * @return 结果
     */
    public int updateSkinStatus(Skin skin); // 修改饰品状态

    /**
     * 同步饰品的参考价（用户端上架时同步到管理端展示价）
     * 
     * @param skin 饰品（id + minPrice）
     * @return 结果
     */
    public int updateMinPrice(Skin skin); // 更新参考价

    /**
     * 删除饰品
     * 
     * @param skinId 饰品ID
     * @return 结果
     */
    public int deleteSkinById(Long skinId); // 删除饰品

    /**
     * 批量删除饰品
     * 
     * @param skinIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSkinByIds(Long[] skinIds); // 批量删除饰品

    /**
     * 查询饰品的关联数据数量（库存/求购/在售）
     * 
     * @param skinId 饰品ID
     * @return 关联数量（key: inventory_count/buy_order_count/listing_count）
     */
    public java.util.Map<String, Object> selectSkinRefCounts(Long skinId); // 查询关联数量
}
