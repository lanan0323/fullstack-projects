// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Java验证注解库
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
// 导入Apache Commons的字符串构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入RuoYi的Excel导出注解
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
// 导入RuoYi框架的通用实体基类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 参数配置表 sys_config
 * 
 * @author ruoyi
 */
public class SysConfig extends BaseEntity // 继承BaseEntity，系统参数配置实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 参数主键 */
    @Excel(name = "参数主键", cellType = ColumnType.NUMERIC) // Excel导出列名为"参数主键"，数字类型
    private Long configId;

    /** 参数名称 */
    @Excel(name = "参数名称") // Excel导出列名为"参数名称"
    private String configName;

    /** 参数键名 */
    @Excel(name = "参数键名") // Excel导出列名为"参数键名"
    private String configKey;

    /** 参数键值 */
    @Excel(name = "参数键值") // Excel导出列名为"参数键值"
    private String configValue;

    /** 系统内置（Y是 N否） */
    @Excel(name = "系统内置", readConverterExp = "Y=是,N=否") // Excel导出时将Y/N转为中文显示
    private String configType;

    public Long getConfigId() // 获取参数主键
    {
        return configId;
    }

    public void setConfigId(Long configId) // 设置参数主键
    {
        this.configId = configId;
    }

    @NotBlank(message = "参数名称不能为空") // 非空校验
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符") // 长度校验：最多100个字符
    public String getConfigName() // 获取参数名称
    {
        return configName;
    }

    public void setConfigName(String configName) // 设置参数名称
    {
        this.configName = configName;
    }

    @NotBlank(message = "参数键名长度不能为空") // 非空校验
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符") // 长度校验：最多100个字符
    public String getConfigKey() // 获取参数键名
    {
        return configKey;
    }

    public void setConfigKey(String configKey) // 设置参数键名
    {
        this.configKey = configKey;
    }

    @NotBlank(message = "参数键值不能为空") // 非空校验
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符") // 长度校验：最多500个字符
    public String getConfigValue() // 获取参数键值
    {
        return configValue;
    }

    public void setConfigValue(String configValue) // 设置参数键值
    {
        this.configValue = configValue;
    }

    public String getConfigType() // 获取参数类型（Y内置/N非内置）
    {
        return configType;
    }

    public void setConfigType(String configType) // 设置参数类型
    {
        this.configType = configType;
    }
    
    @Override
    public String toString() { // 重写toString方法，输出多行格式的对象信息
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId()) // 追加参数主键
            .append("configName", getConfigName()) // 追加参数名称
            .append("configKey", getConfigKey()) // 追加参数键名
            .append("configValue", getConfigValue()) // 追加参数键值
            .append("configType", getConfigType()) // 追加参数类型
            .append("createBy", getCreateBy()) // 追加创建者
            .append("createTime", getCreateTime()) // 追加创建时间
            .append("updateBy", getUpdateBy()) // 追加更新者
            .append("updateTime", getUpdateTime()) // 追加更新时间
            .append("remark", getRemark()) // 追加备注
            .toString();
    }
}
