// 包声明
package com.debuff.system.service;

// 导入集合类和参数配置实体类
import java.util.List;
import com.debuff.system.domain.SysConfig;

/**
 * 参数配置 服务层
 * 
 * @author ruoyi
 */
// 参数配置服务接口，定义参数配置的增删改查及缓存管理方法
public interface ISysConfigService
{
    /**
     * 查询参数配置信息
     * 
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    // 根据ID查询参数配置详情
    public SysConfig selectConfigById(Long configId);

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数键值
     */
    // 根据参数键名查询对应的参数值
    public String selectConfigByKey(String configKey);

    /**
     * 获取验证码开关
     * 
     * @return true开启，false关闭
     */
    // 获取验证码是否开启的配置
    public boolean selectCaptchaEnabled();

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    // 根据条件分页查询参数配置列表
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    // 新增一条参数配置
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    // 修改一条参数配置
    public int updateConfig(SysConfig config);

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     */
    // 批量删除多条参数配置
    public void deleteConfigByIds(Long[] configIds);

    /**
     * 加载参数缓存数据
     */
    // 将所有参数配置加载到Redis缓存中
    public void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    // 清空Redis中的参数缓存
    public void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    // 先清空再重新加载参数缓存
    public void resetConfigCache();

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数信息
     * @return 结果
     */
    // 检查参数键名是否唯一
    public boolean checkConfigKeyUnique(SysConfig config);
}
