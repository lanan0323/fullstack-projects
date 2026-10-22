// 包声明：系统实体类所在包
package com.debuff.common.core.domain.entity;

// 导入JSR303校验注解
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
// 导入Apache Commons的ToString构建器
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
// 导入Excel导出注解
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
// 导入实体基类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 字典类型表 sys_dict_type
 * 
 * @author ruoyi
 */
public class SysDictType extends BaseEntity // 字典类型实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 字典主键 */
    @Excel(name = "字典主键", cellType = ColumnType.NUMERIC) // Excel导出配置：数字类型
    private Long dictId; // 字典类型主键ID

    /** 字典名称 */
    @Excel(name = "字典名称") // Excel导出配置
    private String dictName; // 字典类型名称

    /** 字典类型 */
    @Excel(name = "字典类型") // Excel导出配置
    private String dictType; // 字典类型标识（如：sys_user_sex）

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用") // Excel导出时转换状态值
    private String status; // 状态

    public Long getDictId() { return dictId; } // 获取字典主键

    public void setDictId(Long dictId) { this.dictId = dictId; } // 设置字典主键

    @NotBlank(message = "字典名称不能为空") // 校验名称不能为空
    @Size(min = 0, max = 100, message = "字典类型名称长度不能超过100个字符") // 校验长度
    public String getDictName() { return dictName; } // 获取字典名称

    public void setDictName(String dictName) { this.dictName = dictName; } // 设置字典名称

    @NotBlank(message = "字典类型不能为空") // 校验类型不能为空
    @Size(min = 0, max = 100, message = "字典类型类型长度不能超过100个字符") // 校验长度
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）") // 正则校验格式
    public String getDictType() { return dictType; } // 获取字典类型

    public void setDictType(String dictType) { this.dictType = dictType; } // 设置字典类型

    public String getStatus() { return status; } // 获取状态

    public void setStatus(String status) { this.status = status; } // 设置状态
    
    @Override
    public String toString() { // 重写toString方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dictId", getDictId()) // 字典主键
            .append("dictName", getDictName()) // 字典名称
            .append("dictType", getDictType()) // 字典类型
            .append("status", getStatus()) // 状态
            .append("createBy", getCreateBy()) // 创建者
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateBy", getUpdateBy()) // 更新者
            .append("updateTime", getUpdateTime()) // 更新时间
            .append("remark", getRemark()) // 备注
            .toString();
    }
}
