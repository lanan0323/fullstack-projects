// 包声明
package com.debuff.generator.service;

// 导入集合和实体类
import java.util.List;
import com.debuff.generator.domain.GenTableColumn;

/**
 * 业务字段 服务层
 * 
 * @author ruoyi
 */
// 代码生成字段服务接口
public interface IGenTableColumnService
{
    /**
     * 查询业务字段列表
     * 
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    // 根据表ID查询该表的所有字段配置
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId);

    /**
     * 新增业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    // 新增一个字段配置
    public int insertGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 修改业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    // 更新字段配置
    public int updateGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 删除业务字段信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    // 根据ID字符串批量删除字段配置
    public int deleteGenTableColumnByIds(String ids);
}
