// 包声明
package com.debuff.generator.service;

// 导入集合和Spring注解
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.common.core.text.Convert;
import com.debuff.generator.domain.GenTableColumn;
import com.debuff.generator.mapper.GenTableColumnMapper;

/**
 * 业务字段 服务层实现
 * 
 * @author ruoyi
 */
// 注册为Spring服务组件
@Service
// 代码生成字段服务实现类
public class GenTableColumnServiceImpl implements IGenTableColumnService 
{
	// 注入字段Mapper
	@Autowired
	private GenTableColumnMapper genTableColumnMapper;

	/**
     * 查询业务字段列表
     * 
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
	@Override
	// 根据表ID查询字段列表
	public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId)
	{
	    return genTableColumnMapper.selectGenTableColumnListByTableId(tableId);
	}
	
    /**
     * 新增业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
	@Override
	// 新增字段记录
	public int insertGenTableColumn(GenTableColumn genTableColumn)
	{
	    return genTableColumnMapper.insertGenTableColumn(genTableColumn);
	}
	
	/**
     * 修改业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
	@Override
	// 更新字段记录
	public int updateGenTableColumn(GenTableColumn genTableColumn)
	{
	    return genTableColumnMapper.updateGenTableColumn(genTableColumn);
	}

	/**
     * 删除业务字段对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	@Override
	// 将逗号分隔的ID字符串转为Long数组后批量删除
	public int deleteGenTableColumnByIds(String ids)
	{
		return genTableColumnMapper.deleteGenTableColumnByIds(Convert.toLongArray(ids));
	}
}
