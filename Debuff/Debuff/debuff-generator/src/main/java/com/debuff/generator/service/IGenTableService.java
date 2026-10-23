// 包声明
package com.debuff.generator.service;

// 导入集合和实体类
import java.util.List;
import java.util.Map;
import com.debuff.generator.domain.GenTable;

/**
 * 业务 服务层
 * 
 * @author ruoyi
 */
// 代码生成表服务接口
public interface IGenTableService
{
    /**
     * 查询业务列表
     * 
     * @param genTable 业务信息
     * @return 业务集合
     */
    // 查询已导入的代码生成表列表
    public List<GenTable> selectGenTableList(GenTable genTable);

    /**
     * 查询据库列表
     * 
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    // 查询数据库中所有用户表(未导入的)
    public List<GenTable> selectDbTableList(GenTable genTable);

    /**
     * 查询据库列表
     * 
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    // 根据表名数组查询对应的数据库表信息
    public List<GenTable> selectDbTableListByNames(String[] tableNames);

    /**
     * 查询所有表信息
     * 
     * @return 表信息集合
     */
    // 查询所有已导入的代码生成表
    public List<GenTable> selectGenTableAll();

    /**
     * 查询业务信息
     * 
     * @param id 业务ID
     * @return 业务信息
     */
    // 根据ID查询表详情(含字段列表)
    public GenTable selectGenTableById(Long id);

    /**
     * 修改业务
     * 
     * @param genTable 业务信息
     * @return 结果
     */
    // 更新代码生成表配置(含字段配置)
    public void updateGenTable(GenTable genTable);

    /**
     * 删除业务信息
     * 
     * @param tableIds 需要删除的表数据ID
     * @return 结果
     */
    // 根据ID数组删除代码生成表及关联字段
    public void deleteGenTableByIds(Long[] tableIds);

    /**
     * 导入表结构
     * 
     * @param tableList 导入表列表
     */
    // 将数据库表结构导入到代码生成系统
    public void importGenTable(List<GenTable> tableList);

    /**
     * 预览代码
     * 
     * @param tableId 表编号
     * @return 预览数据列表
     */
    // 根据表ID预览生成的代码(返回文件名-代码键值对)
    public Map<String, String> previewCode(Long tableId);

    /**
     * 生成代码（下载方式）
     * 
     * @param tableName 表名称
     * @return 数据
     */
    // 生成单表代码并返回ZIP字节流供下载
    public byte[] downloadCode(String tableName);

    /**
     * 生成代码（自定义路径）
     * 
     * @param tableName 表名称
     * @return 数据
     */
    // 生成代码并写入到自定义路径
    public void generatorCode(String tableName);

    /**
     * 同步数据库
     * 
     * @param tableName 表名称
     */
    // 同步数据库表结构与代码生成表结构
    public void synchDb(String tableName);

    /**
     * 批量生成代码（下载方式）
     * 
     * @param tableNames 表数组
     * @return 数据
     */
    // 批量生成多表代码并返回ZIP字节流
    public byte[] downloadCode(String[] tableNames);

    /**
     * 修改保存参数校验
     * 
     * @param genTable 业务信息
     */
    // 校验编辑时的参数合法性
    public void validateEdit(GenTable genTable);
}
