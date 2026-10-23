// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.entity.SkinQuality;

/**
 * 饰品品质 数据层
 * 
 * @author B同学
 */
public interface SkinQualityMapper // 饰品品质Mapper接口，定义数据库操作方法
{
    /**
     * 查询饰品品质列表
     * 
     * @param skinQuality 饰品品质
     * @return 饰品品质集合
     */
    public List<SkinQuality> selectSkinQualityList(SkinQuality skinQuality);

    /**
     * 查询全部品质（按排序号）
     * 
     * @return 饰品品质集合
     */
    public List<SkinQuality> selectAllSkinQualities();

    /**
     * 通过ID查询饰品品质
     * 
     * @param id 饰品品质ID
     * @return 饰品品质
     */
    public SkinQuality selectSkinQualityById(Long id);

    /**
     * 新增饰品品质
     * 
     * @param skinQuality 饰品品质
     * @return 结果
     */
    public int insertSkinQuality(SkinQuality skinQuality);

    /**
     * 修改饰品品质
     * 
     * @param skinQuality 饰品品质
     * @return 结果
     */
    public int updateSkinQuality(SkinQuality skinQuality);

    /**
     * 删除饰品品质
     * 
     * @param id 饰品品质ID
     * @return 结果
     */
    public int deleteSkinQualityById(Long id);

    /**
     * 批量删除饰品品质
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSkinQualityByIds(Long[] ids);
}
