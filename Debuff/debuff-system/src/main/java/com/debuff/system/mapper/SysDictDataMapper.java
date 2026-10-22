// 包声明
package com.debuff.system.mapper;

// 导入集合、参数注解和字典数据实体类
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.debuff.common.core.domain.entity.SysDictData;

/**
 * 字典表 数据层
 * 
 * @author ruoyi
 */
// 字典数据Mapper接口，定义字典数据的增删改查方法
public interface SysDictDataMapper
{
    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    // 根据条件分页查询字典数据列表
    public List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    // 根据字典类型查询对应的所有字典数据
    public List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    // 根据字典类型和键值查询字典标签
    public String selectDictLabel(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    // 根据ID查询单条字典数据详情
    public SysDictData selectDictDataById(Long dictCode);

    /**
     * 查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据
     */
    // 统计指定字典类型下的数据数量
    public int countDictDataByType(String dictType);

    /**
     * 通过字典ID删除字典数据信息
     * 
     * @param dictCode 字典数据ID
     * @return 结果
     */
    // 根据ID删除单条字典数据
    public int deleteDictDataById(Long dictCode);

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    // 批量删除多条字典数据
    public int deleteDictDataByIds(Long[] dictCodes);

    /**
     * 新增字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    // 新增一条字典数据
    public int insertDictData(SysDictData dictData);

    /**
     * 修改字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    // 修改一条字典数据
    public int updateDictData(SysDictData dictData);

    /**
     * 同步修改字典类型
     * 
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
    // 当字典类型变更时同步更新关联的字典数据
    public int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);
}
