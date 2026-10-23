// 包声明：系统模块的实体类包
package com.debuff.system.domain;

// 导入Java日期类
import java.util.Date;
// 导入Jackson的JSON日期格式化注解
import com.fasterxml.jackson.annotation.JsonFormat;
// 导入RuoYi的Excel导出注解
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
// 导入RuoYi框架的通用实体基类
import com.debuff.common.core.domain.BaseEntity;

/**
 * 操作日志记录表 oper_log
 * 
 * @author ruoyi
 */
public class SysOperLog extends BaseEntity // 继承BaseEntity，记录系统操作日志
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 日志主键 */
    @Excel(name = "操作序号", cellType = ColumnType.NUMERIC) // Excel导出列名为"操作序号"，数字类型
    private Long operId;

    /** 操作模块 */
    @Excel(name = "操作模块") // Excel导出列名为"操作模块"
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @Excel(name = "业务类型", readConverterExp = "0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据") // Excel导出时将业务类型码转为中文
    private Integer businessType;

    /** 业务类型数组 */
    private Integer[] businessTypes; // 用于查询时传入多个业务类型条件

    /** 请求方法 */
    @Excel(name = "请求方法") // Excel导出列名为"请求方法"
    private String method;

    /** 请求方式 */
    @Excel(name = "请求方式") // Excel导出列名为"请求方式"
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @Excel(name = "操作类别", readConverterExp = "0=其它,1=后台用户,2=手机端用户") // Excel导出时将操作类别码转为中文
    private Integer operatorType;

    /** 操作人员 */
    @Excel(name = "操作人员") // Excel导出列名为"操作人员"
    private String operName;

    /** 部门名称 */
    @Excel(name = "部门名称") // Excel导出列名为"部门名称"
    private String deptName;

    /** 请求url */
    @Excel(name = "请求地址") // Excel导出列名为"请求地址"
    private String operUrl;

    /** 操作地址 */
    @Excel(name = "操作地址") // Excel导出列名为"操作地址"
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作地点") // Excel导出列名为"操作地点"
    private String operLocation;

    /** 请求参数 */
    @Excel(name = "请求参数") // Excel导出列名为"请求参数"
    private String operParam;

    /** 返回参数 */
    @Excel(name = "返回参数") // Excel导出列名为"返回参数"
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常") // Excel导出时将状态码转为中文
    private Integer status;

    /** 错误消息 */
    @Excel(name = "错误消息") // Excel导出列名为"错误消息"
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON序列化时格式化日期
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss") // Excel导出时设置日期格式
    private Date operTime;

    /** 消耗时间 */
    @Excel(name = "消耗时间", suffix = "毫秒") // Excel导出时后缀显示"毫秒"
    private Long costTime;

    public Long getOperId() // 获取日志主键
    {
        return operId;
    }

    public void setOperId(Long operId) // 设置日志主键
    {
        this.operId = operId;
    }

    public String getTitle() // 获取操作模块名称
    {
        return title;
    }

    public void setTitle(String title) // 设置操作模块名称
    {
        this.title = title;
    }

    public Integer getBusinessType() // 获取业务类型
    {
        return businessType;
    }

    public void setBusinessType(Integer businessType) // 设置业务类型
    {
        this.businessType = businessType;
    }

    public Integer[] getBusinessTypes() // 获取业务类型数组（查询条件用）
    {
        return businessTypes;
    }

    public void setBusinessTypes(Integer[] businessTypes) // 设置业务类型数组
    {
        this.businessTypes = businessTypes;
    }

    public String getMethod() // 获取请求方法（类名.方法名）
    {
        return method;
    }

    public void setMethod(String method) // 设置请求方法
    {
        this.method = method;
    }

    public String getRequestMethod() // 获取请求方式（GET/POST等）
    {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) // 设置请求方式
    {
        this.requestMethod = requestMethod;
    }

    public Integer getOperatorType() // 获取操作类别
    {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) // 设置操作类别
    {
        this.operatorType = operatorType;
    }

    public String getOperName() // 获取操作人员
    {
        return operName;
    }

    public void setOperName(String operName) // 设置操作人员
    {
        this.operName = operName;
    }

    public String getDeptName() // 获取部门名称
    {
        return deptName;
    }

    public void setDeptName(String deptName) // 设置部门名称
    {
        this.deptName = deptName;
    }

    public String getOperUrl() // 获取请求URL
    {
        return operUrl;
    }

    public void setOperUrl(String operUrl) // 设置请求URL
    {
        this.operUrl = operUrl;
    }

    public String getOperIp() // 获取操作IP地址
    {
        return operIp;
    }

    public void setOperIp(String operIp) // 设置操作IP地址
    {
        this.operIp = operIp;
    }

    public String getOperLocation() // 获取操作地点
    {
        return operLocation;
    }

    public void setOperLocation(String operLocation) // 设置操作地点
    {
        this.operLocation = operLocation;
    }

    public String getOperParam() // 获取请求参数
    {
        return operParam;
    }

    public void setOperParam(String operParam) // 设置请求参数
    {
        this.operParam = operParam;
    }

    public String getJsonResult() // 获取返回结果（JSON格式）
    {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) // 设置返回结果
    {
        this.jsonResult = jsonResult;
    }

    public Integer getStatus() // 获取操作状态
    {
        return status;
    }

    public void setStatus(Integer status) // 设置操作状态
    {
        this.status = status;
    }

    public String getErrorMsg() // 获取错误消息
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) // 设置错误消息
    {
        this.errorMsg = errorMsg;
    }

    public Date getOperTime() // 获取操作时间
    {
        return operTime;
    }

    public void setOperTime(Date operTime) // 设置操作时间
    {
        this.operTime = operTime;
    }

    public Long getCostTime() // 获取消耗时间（毫秒）
    {
        return costTime;
    }

    public void setCostTime(Long costTime) // 设置消耗时间
    {
        this.costTime = costTime;
    }
}
