// 包声明：系统模块的Mapper数据访问层
package com.debuff.system.mapper;

// 导入Java集合类
import java.util.List;
// 导入系统参数配置实体类
import com.debuff.system.domain.SysConfig;

/**
 * 参数配置 数据层
 * 
 * @author ruoyi
 */
public interface SysConfigMapper // 参数配置Mapper接口，定义数据库操作的方法
{
    /**
     * 查询参数配置信息
     * 
     * @param config 参数配置信息
     * @return 参数配置信息
     */
    public SysConfig selectConfig(SysConfig config); // 根据条件查询单条参数配置

    /**
     * 通过ID查询配置
     * 
     * @param configId 参数ID
     * @return 参数配置信息
     */
    public SysConfig selectConfigById(Long configId); // 根据参数ID查询参数配置

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config); // 分页查询参数配置列表

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数配置信息
     */
    public SysConfig checkConfigKeyUnique(String configKey); // 校验参数键名是否唯一

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int insertConfig(SysConfig config); // 新增参数配置记录

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int updateConfig(SysConfig config); // 更新参数配置记录

    /**
     * 删除参数配置
     * 
     * @param configId 参数ID
     * @return 结果
     */
    public int deleteConfigById(Long configId); // 根据ID删除单条参数配置

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    public int deleteConfigByIds(Long[] configIds); // 批量删除参数配置记录
}
