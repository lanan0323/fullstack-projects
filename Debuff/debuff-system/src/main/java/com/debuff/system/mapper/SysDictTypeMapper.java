// 包声明
package com.debuff.system.mapper;

// 导入集合类和字典类型实体类
import java.util.List;
import com.debuff.common.core.domain.entity.SysDictType;

/**
 * 字典表 数据层
 * 
 * @author ruoyi
 */
// 字典类型Mapper接口，定义字典类型的增删改查方法
public interface SysDictTypeMapper
{
    /**
     * 根据条件分页查询字典类型
     * 
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    // 根据条件分页查询字典类型列表
    public List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 根据所有字典类型
     * 
     * @return 字典类型集合信息
     */
    // 查询所有字典类型
    public List<SysDictType> selectDictTypeAll();

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    // 根据ID查询单条字典类型详情
    public SysDictType selectDictTypeById(Long dictId);

    /**
     * 根据字典类型查询信息
     * 
     * @param dictType 字典类型
     * @return 字典类型
     */
    // 根据字典类型字符串查询字典类型信息
    public SysDictType selectDictTypeByType(String dictType);

    /**
     * 通过字典ID删除字典信息
     * 
     * @param dictId 字典ID
     * @return 结果
     */
    // 根据ID删除单条字典类型
    public int deleteDictTypeById(Long dictId);

    /**
     * 批量删除字典类型信息
     * 
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    // 批量删除多条字典类型
    public int deleteDictTypeByIds(Long[] dictIds);

    /**
     * 新增字典类型信息
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    // 新增一条字典类型
    public int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型信息
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    // 修改一条字典类型
    public int updateDictType(SysDictType dictType);

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dictType 字典类型
     * @return 结果
     */
    // 检查字典类型是否唯一，返回null表示唯一
    public SysDictType checkDictTypeUnique(String dictType);
}
