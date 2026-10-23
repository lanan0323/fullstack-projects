// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import java.util.Map;
import com.debuff.trade.domain.entity.Skin;
import com.debuff.trade.domain.query.SkinMarketQuery;
import com.debuff.trade.domain.vo.SkinDetailVO;
import com.debuff.trade.domain.vo.SkinMarketVO;

/**
 * 饰品 服务层
 * 
 * @author B同学
 */
public interface ISkinService // 饰品服务接口，定义饰品管理相关的所有业务方法
{
    /**
     * 查询饰品列表（后台管理）
     * 
     * @param skin 饰品
     * @return 饰品集合
     */
    public List<Skin> selectSkinList(Skin skin);

    /**
     * 通过ID查询饰品
     * 
     * @param skinId 饰品ID
     * @return 饰品
     */
    public Skin selectSkinById(Long skinId);

    /**
     * 查询饰品市场列表
     * 
     * @param query 查询条件
     * @return 市场列表VO集合
     */
    public List<SkinMarketVO> selectMarketList(SkinMarketQuery query);

    /**
     * 查询热门饰品
     * 
     * @param limit 条数
     * @return 市场列表VO集合
     */
    public List<SkinMarketVO> selectHotSkins(int limit);

    /**
     * 获取市场分类数据（武器分类 + 品质列表）
     * 
     * @return 分类数据
     */
    public Map<String, Object> selectCategories();

    /**
     * 查询饰品详情（基本信息 + 在售列表 + 价格历史）
     * 
     * @param skinId 饰品ID
     * @return 饰品详情
     */
    public SkinDetailVO selectSkinDetail(Long skinId);

    /**
     * 查询饰品价格历史
     * 
     * @param skinId 饰品ID
     * @return 价格历史集合
     */
    public List<SkinDetailVO.PricePoint> selectPriceHistory(Long skinId);

    /**
     * 新增饰品
     * 
     * @param skin 饰品
     * @return 结果
     */
    public int insertSkin(Skin skin);

    /**
     * 修改饰品
     * 
     * @param skin 饰品
     * @return 结果
     */
    public int updateSkin(Skin skin);

    /**
     * 修改饰品状态（上架/下架）
     * 
     * @param skin 饰品
     * @return 结果
     */
    public int updateSkinStatus(Skin skin);

    /**
     * 批量删除饰品
     * 
     * @param skinIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSkinByIds(Long[] skinIds);
}
