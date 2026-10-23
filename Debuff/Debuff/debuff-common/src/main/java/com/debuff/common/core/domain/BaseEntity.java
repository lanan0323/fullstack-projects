// 包声明：核心域对象所在包
package com.debuff.common.core.domain;

// 导入Serializable接口
import java.io.Serializable;
// 导入Date日期类型
import java.util.Date;
// 导入HashMap
import java.util.HashMap;
// 导入Map接口
import java.util.Map;
// 导入JSON格式化注解
import com.fasterxml.jackson.annotation.JsonFormat;
// 导入JSON忽略注解
import com.fasterxml.jackson.annotation.JsonIgnore;
// 导入JSON包含控制注解
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Entity基类
 * 
 * @author ruoyi
 */
public class BaseEntity implements Serializable // 所有实体的基类，包含公共字段
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 搜索值 */
    @JsonIgnore // JSON序列化时忽略该字段
    private String searchValue; // 搜索值，不传递给前端

    /** 创建者 */
    private String createBy; // 创建人

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期格式化格式
    private Date createTime; // 创建时间

    /** 更新者 */
    private String updateBy; // 更新人

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 指定日期格式化格式
    private Date updateTime; // 更新时间

    /** 备注 */
    private String remark; // 备注信息

    /** 请求参数 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 为空时不序列化到JSON
    private Map<String, Object> params; // 请求参数Map，用于传递分页等额外参数

    public String getSearchValue() { return searchValue; } // 获取搜索值

    public void setSearchValue(String searchValue) { this.searchValue = searchValue; } // 设置搜索值

    public String getCreateBy() { return createBy; } // 获取创建人

    public void setCreateBy(String createBy) { this.createBy = createBy; } // 设置创建人

    public Date getCreateTime() { return createTime; } // 获取创建时间

    public void setCreateTime(Date createTime) { this.createTime = createTime; } // 设置创建时间

    public String getUpdateBy() { return updateBy; } // 获取更新人

    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; } // 设置更新人

    public Date getUpdateTime() { return updateTime; } // 获取更新时间

    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; } // 设置更新时间

    public String getRemark() { return remark; } // 获取备注

    public void setRemark(String remark) { this.remark = remark; } // 设置备注

    public Map<String, Object> getParams() // 获取请求参数Map
    {
        if (params == null) // 延迟初始化
        {
            params = new HashMap<>(); // 为空时创建新Map
        }
        return params; // 返回参数Map
    }

    public void setParams(Map<String, Object> params) { this.params = params; } // 设置请求参数Map
}
