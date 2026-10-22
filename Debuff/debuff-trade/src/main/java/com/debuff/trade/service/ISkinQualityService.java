// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.entity.SkinQuality;

/**
 * 饰品品质 服务层
 * 
 * @author B同学
 */
public interface ISkinQualityService // 饰品品质服务接口，定义品质管理业务方法
{
    /**
     * 查询饰品品质列表
     * 
     * @param skinQuality 饰品品质
     * @return 饰品品质集合
     */
    public List<SkinQuality> selectSkinQualityList(SkinQuality skinQuality);

    /**
     * 查询全部品质
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
     * 批量删除饰品品质
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSkinQualityByIds(Long[] ids);
}
