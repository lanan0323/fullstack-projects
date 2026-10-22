// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.trade.domain.entity.SkinQuality;
import com.debuff.trade.mapper.SkinQualityMapper;
import com.debuff.trade.service.ISkinQualityService;

/**
 * 饰品品质 服务层实现
 * 
 * @author B同学
 */
@Service // 标记为Spring服务组件
public class SkinQualityServiceImpl implements ISkinQualityService // 实现饰品品质服务接口
{
    @Autowired // 自动注入品质Mapper
    private SkinQualityMapper skinQualityMapper;

    @Override // 查询品质列表
    public List<SkinQuality> selectSkinQualityList(SkinQuality skinQuality)
    {
        return skinQualityMapper.selectSkinQualityList(skinQuality); // 委托给Mapper执行查询
    }

    @Override // 查询全部品质
    public List<SkinQuality> selectAllSkinQualities()
    {
        return skinQualityMapper.selectAllSkinQualities(); // 委托给Mapper执行查询
    }

    @Override // 根据ID查询品质
    public SkinQuality selectSkinQualityById(Long id)
    {
        return skinQualityMapper.selectSkinQualityById(id); // 委托给Mapper执行查询
    }

    @Override // 新增品质
    public int insertSkinQuality(SkinQuality skinQuality)
    {
        return skinQualityMapper.insertSkinQuality(skinQuality); // 委托给Mapper执行新增
    }

    @Override // 修改品质
    public int updateSkinQuality(SkinQuality skinQuality)
    {
        return skinQualityMapper.updateSkinQuality(skinQuality); // 委托给Mapper执行更新
    }

    @Override // 批量删除品质
    public int deleteSkinQualityByIds(Long[] ids)
    {
        return skinQualityMapper.deleteSkinQualityByIds(ids); // 委托给Mapper执行批量删除
    }
}
