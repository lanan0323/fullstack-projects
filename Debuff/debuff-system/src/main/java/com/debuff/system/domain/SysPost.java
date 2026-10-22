// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Java验证注解库
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
 * 岗位表 sys_post
 * 
 * @author ruoyi
 */
public class SysPost extends BaseEntity // 继承BaseEntity，获得创建人、创建时间等公共字段
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 岗位序号 */
    @Excel(name = "岗位序号", cellType = ColumnType.NUMERIC) // Excel导出时列名为"岗位序号"，类型为数字
    private Long postId;

    /** 岗位编码 */
    @Excel(name = "岗位编码") // Excel导出时列名为"岗位编码"
    private String postCode;

    /** 岗位名称 */
    @Excel(name = "岗位名称") // Excel导出时列名为"岗位名称"
    private String postName;

    /** 岗位排序 */
    @Excel(name = "岗位排序") // Excel导出时列名为"岗位排序"
    private Integer postSort;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用") // Excel导出时将状态码转为中文显示
    private String status;

    /** 用户是否存在此岗位标识 默认不存在 */
    private boolean flag = false; // 辅助标记，用于前端显示用户是否已关联该岗位

    public Long getPostId() // 获取岗位ID
    {
        return postId;
    }

    public void setPostId(Long postId) // 设置岗位ID
    {
        this.postId = postId;
    }

    @NotBlank(message = "岗位编码不能为空") // 非空校验
    @Size(min = 0, max = 64, message = "岗位编码长度不能超过64个字符") // 长度校验：最多64个字符
    public String getPostCode() // 获取岗位编码
    {
        return postCode;
    }

    public void setPostCode(String postCode) // 设置岗位编码
    {
        this.postCode = postCode;
    }

    @NotBlank(message = "岗位名称不能为空") // 非空校验
    @Size(min = 0, max = 50, message = "岗位名称长度不能超过50个字符") // 长度校验：最多50个字符
    public String getPostName() // 获取岗位名称
    {
        return postName;
    }

    public void setPostName(String postName) // 设置岗位名称
    {
        this.postName = postName;
    }

    @NotNull(message = "显示顺序不能为空") // 非空校验
    public Integer getPostSort() // 获取岗位排序值
    {
        return postSort;
    }

    public void setPostSort(Integer postSort) // 设置岗位排序值
    {
        this.postSort = postSort;
    }

    public String getStatus() // 获取岗位状态
    {
        return status;
    }

    public void setStatus(String status) // 设置岗位状态
    {
        this.status = status;
    }

    public boolean isFlag() // 获取岗位关联标记
    {
        return flag;
    }

    public void setFlag(boolean flag) // 设置岗位关联标记
    {
        this.flag = flag;
    }
    
    @Override
    public String toString() { // 重写toString方法，输出多行格式的对象信息
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("postId", getPostId()) // 追加岗位ID
            .append("postCode", getPostCode()) // 追加岗位编码
            .append("postName", getPostName()) // 追加岗位名称
            .append("postSort", getPostSort()) // 追加排序值
            .append("status", getStatus()) // 追加状态
            .append("createBy", getCreateBy()) // 追加创建者
            .append("createTime", getCreateTime()) // 追加创建时间
            .append("updateBy", getUpdateBy()) // 追加更新者
            .append("updateTime", getUpdateTime()) // 追加更新时间
            .append("remark", getRemark()) // 追加备注
            .toString(); // 转换为字符串
    }
}
