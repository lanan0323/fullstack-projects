// 包声明
package com.debuff.system.service;

// 导入集合类、字典数据和字典类型实体类
import java.util.List;
import com.debuff.common.core.domain.entity.SysDictData;
import com.debuff.common.core.domain.entity.SysDictType;

/**
 * 字典 业务层
 * 
 * @author ruoyi
 */
// 字典类型服务接口，定义字典类型的增删改查及缓存管理方法
public interface ISysDictTypeService
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
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    // 根据字典类型查询对应的字典数据列表
    public List<SysDictData> selectDictDataByType(String dictType);

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
     * 批量删除字典信息
     * 
     * @param dictIds 需要删除的字典ID
     */
    // 批量删除多条字典类型
    public void deleteDictTypeByIds(Long[] dictIds);

    /**
     * 加载字典缓存数据
     */
    // 将所有字典数据加载到Redis缓存中
    public void loadingDictCache();

    /**
     * 清空字典缓存数据
     */
    // 清空Redis中的字典缓存
    public void clearDictCache();

    /**
     * 重置字典缓存数据
     */
    // 先清空再重新加载字典缓存
    public void resetDictCache();

    /**
     * 新增保存字典类型信息
     * 
     * @param dictType 字典类型信息
     * @return 结果
     */
    // 新增一条字典类型
    public int insertDictType(SysDictType dictType);

    /**
     * 修改保存字典类型信息
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
    // 检查字典类型是否唯一
    public boolean checkDictTypeUnique(SysDictType dictType);
}
