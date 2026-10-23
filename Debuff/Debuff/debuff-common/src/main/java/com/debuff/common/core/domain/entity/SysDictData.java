// 包声明：系统实体类所在包
package com.debuff.common.core.domain.entity;

// 导入JSR303校验注解
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
// 导入Apache Commons的ToString构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入Excel导出注解
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
// 导入用户常量
import com.debuff.common.constant.UserConstants;
// 导入实体基类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 字典数据表 sys_dict_data
 * 
 * @author ruoyi
 */
public class SysDictData extends BaseEntity // 字典数据实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 字典编码 */
    @Excel(name = "字典编码", cellType = ColumnType.NUMERIC) // Excel导出配置：数字类型
    private Long dictCode; // 字典数据主键

    /** 字典排序 */
    @Excel(name = "字典排序", cellType = ColumnType.NUMERIC) // Excel导出配置：数字类型
    private Long dictSort; // 显示排序

    /** 字典标签 */
    @Excel(name = "字典标签") // Excel导出配置
    private String dictLabel; // 字典标签（显示值）

    /** 字典键值 */
    @Excel(name = "字典键值") // Excel导出配置
    private String dictValue; // 字典键值（存储值）

    /** 字典类型 */
    @Excel(name = "字典类型") // Excel导出配置
    private String dictType; // 所属字典类型

    /** 样式属性（其他样式扩展） */
    private String cssClass; // CSS样式类名

    /** 表格字典样式 */
    private String listClass; // 列表显示样式

    /** 是否默认（Y是 N否） */
    @Excel(name = "是否默认", readConverterExp = "Y=是,N=否") // Excel导出时转换
    private String isDefault; // 是否默认值

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用") // Excel导出时转换
    private String status; // 状态

    public Long getDictCode() { return dictCode; } // 获取字典编码

    public void setDictCode(Long dictCode) { this.dictCode = dictCode; } // 设置字典编码

    public Long getDictSort() { return dictSort; } // 获取排序

    public void setDictSort(Long dictSort) { this.dictSort = dictSort; } // 设置排序

    @NotBlank(message = "字典标签不能为空") // 校验标签不能为空
    @Size(min = 0, max = 100, message = "字典标签长度不能超过100个字符") // 校验长度
    public String getDictLabel() { return dictLabel; } // 获取字典标签

    public void setDictLabel(String dictLabel) { this.dictLabel = dictLabel; } // 设置字典标签

    @NotBlank(message = "字典键值不能为空") // 校验键值不能为空
    @Size(min = 0, max = 100, message = "字典键值长度不能超过100个字符") // 校验长度
    public String getDictValue() { return dictValue; } // 获取字典键值

    public void setDictValue(String dictValue) { this.dictValue = dictValue; } // 设置字典键值

    @NotBlank(message = "字典类型不能为空") // 校验类型不能为空
    @Size(min = 0, max = 100, message = "字典类型长度不能超过100个字符") // 校验长度
    public String getDictType() { return dictType; } // 获取字典类型

    public void setDictType(String dictType) { this.dictType = dictType; } // 设置字典类型

    @Size(min = 0, max = 100, message = "样式属性长度不能超过100个字符") // 校验长度
    public String getCssClass() { return cssClass; } // 获取CSS样式

    public void setCssClass(String cssClass) { this.cssClass = cssClass; } // 设置CSS样式

    public String getListClass() { return listClass; } // 获取列表样式

    public void setListClass(String listClass) { this.listClass = listClass; } // 设置列表样式

    public boolean getDefault() // 判断是否为默认值
    {
        return UserConstants.YES.equals(this.isDefault); // 比较是否为"Y"
    }

    public String getIsDefault() { return isDefault; } // 获取是否默认标识

    public void setIsDefault(String isDefault) { this.isDefault = isDefault; } // 设置是否默认标识

    public String getStatus() { return status; } // 获取状态

    public void setStatus(String status) { this.status = status; } // 设置状态
    
    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dictCode", getDictCode()) // 字典编码
            .append("dictSort", getDictSort()) // 排序
            .append("dictLabel", getDictLabel()) // 标签
            .append("dictValue", getDictValue()) // 键值
            .append("dictType", getDictType()) // 类型
            .append("cssClass", getCssClass()) // CSS样式
            .append("listClass", getListClass()) // 列表样式
            .append("isDefault", getIsDefault()) // 是否默认
            .append("status", getStatus()) // 状态
            .append("createBy", getCreateBy()) // 创建者
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateBy", getUpdateBy()) // 更新者
            .append("updateTime", getUpdateTime()) // 更新时间
            .append("remark", getRemark()) // 备注
            .toString();
    }
}
