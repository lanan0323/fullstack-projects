// 包声明
package com.debuff.generator.mapper;

// 导入所需类
import java.util.List;
import com.debuff.generator.domain.GenTable;

/**
 * 业务 数据层
 * 
 * @author ruoyi
 */
public interface GenTableMapper // 代码生成表Mapper接口
{
    /**
     * 查询业务列表
     * 
     * @param genTable 业务信息
     * @return 业务集合
     */
    public List<GenTable> selectGenTableList(GenTable genTable); // 条件查询代码生成表列表

    /**
     * 查询据库列表
     * 
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableList(GenTable genTable); // 查询数据库中的表列表

    /**
     * 查询据库列表
     * 
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    public List<GenTable> selectDbTableListByNames(String[] tableNames); // 根据表名数组查询数据库表

    /**
     * 查询所有表信息
     * 
     * @return 表信息集合
     */
    public List<GenTable> selectGenTableAll(); // 查询所有代码生成表

    /**
     * 查询表ID业务信息
     * 
     * @param id 业务ID
     * @return 业务信息
     */
    public GenTable selectGenTableById(Long id); // 根据ID查询代码生成表

    /**
     * 查询表名称业务信息
     * 
     * @param tableName 表名称
     * @return 业务信息
     */
    public GenTable selectGenTableByName(String tableName); // 根据表名查询代码生成表

    /**
     * 新增业务
     * 
     * @param genTable 业务信息
     * @return 结果
     */
    public int insertGenTable(GenTable genTable); // 插入代码生成表

    /**
     * 修改业务
     * 
     * @param genTable 业务信息
     * @return 结果
     */
    public int updateGenTable(GenTable genTable); // 更新代码生成表

    /**
     * 批量删除业务
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGenTableByIds(Long[] ids); // 批量删除代码生成表
}
