// 包声明
package com.debuff.generator.mapper;

// 导入集合和实体类
import java.util.List;
import com.debuff.generator.domain.GenTableColumn;

/**
 * 业务字段 数据层
 * 
 * @author ruoyi
 */
// 代码生成列Mapper接口
public interface GenTableColumnMapper
{
    /**
     * 根据表名称查询列信息
     * 
     * @param tableName 表名称
     * @return 列信息
     */
    // 根据表名查询数据库列信息
    public List<GenTableColumn> selectDbTableColumnsByName(String tableName);

    /**
     * 查询业务字段列表
     * 
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    // 根据表ID查询已导入的字段列表
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId);

    /**
     * 新增业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    // 插入一条字段记录
    public int insertGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 修改业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    // 更新字段信息
    public int updateGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 删除业务字段
     * 
     * @param genTableColumns 列数据
     * @return 结果
     */
    // 根据字段对象列表批量删除
    public int deleteGenTableColumns(List<GenTableColumn> genTableColumns);

    /**
     * 批量删除业务字段
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    // 根据ID数组批量删除字段
    public int deleteGenTableColumnByIds(Long[] ids);
}
