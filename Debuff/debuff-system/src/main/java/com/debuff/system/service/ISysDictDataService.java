// 包声明
package com.debuff.system.service;

// 导入集合类和字典数据实体类
import java.util.List;
import com.debuff.common.core.domain.entity.SysDictData;

/**
 * 字典 业务层
 * 
 * @author ruoyi
 */
// 字典数据服务接口，定义字典数据的增删改查方法
public interface ISysDictDataService
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
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    // 根据字典类型和键值查询字典标签
    public String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    // 根据ID查询单条字典数据详情
    public SysDictData selectDictDataById(Long dictCode);

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID
     */
    // 批量删除多条字典数据
    public void deleteDictDataByIds(Long[] dictCodes);

    /**
     * 新增保存字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    // 新增一条字典数据
    public int insertDictData(SysDictData dictData);

    /**
     * 修改保存字典数据信息
     * 
     * @param dictData 字典数据信息
     * @return 结果
     */
    // 修改一条字典数据
    public int updateDictData(SysDictData dictData);
}
