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
 * 系统访问记录表 sys_logininfor
 * 
 * @author ruoyi
 */
public class SysLogininfor extends BaseEntity // 继承BaseEntity，记录登录日志信息
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** ID */
    @Excel(name = "序号", cellType = ColumnType.NUMERIC) // Excel导出列名为"序号"，数字类型
    private Long infoId;

    /** 用户账号 */
    @Excel(name = "用户账号") // Excel导出列名为"用户账号"
    private String userName;

    /** 登录状态 0成功 1失败 */
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败") // Excel导出时将状态码转为中文
    private String status;

    /** 登录IP地址 */
    @Excel(name = "登录地址") // Excel导出列名为"登录地址"
    private String ipaddr;

    /** 登录地点 */
    @Excel(name = "登录地点") // Excel导出列名为"登录地点"
    private String loginLocation;

    /** 浏览器类型 */
    @Excel(name = "浏览器") // Excel导出列名为"浏览器"
    private String browser;

    /** 操作系统 */
    @Excel(name = "操作系统") // Excel导出列名为"操作系统"
    private String os;

    /** 提示消息 */
    @Excel(name = "提示消息") // Excel导出列名为"提示消息"
    private String msg;

    /** 访问时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // JSON序列化时格式化日期
    @Excel(name = "访问时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss") // Excel导出时设置日期格式
    private Date loginTime;

    public Long getInfoId() // 获取登录记录ID
    {
        return infoId;
    }

    public void setInfoId(Long infoId) // 设置登录记录ID
    {
        this.infoId = infoId;
    }

    public String getUserName() // 获取用户账号
    {
        return userName;
    }

    public void setUserName(String userName) // 设置用户账号
    {
        this.userName = userName;
    }

    public String getStatus() // 获取登录状态
    {
        return status;
    }

    public void setStatus(String status) // 设置登录状态
    {
        this.status = status;
    }

    public String getIpaddr() // 获取登录IP地址
    {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) // 设置登录IP地址
    {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() // 获取登录地点
    {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) // 设置登录地点
    {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() // 获取浏览器类型
    {
        return browser;
    }

    public void setBrowser(String browser) // 设置浏览器类型
    {
        this.browser = browser;
    }

    public String getOs() // 获取操作系统
    {
        return os;
    }

    public void setOs(String os) // 设置操作系统
    {
        this.os = os;
    }

    public String getMsg() // 获取提示消息
    {
        return msg;
    }

    public void setMsg(String msg) // 设置提示消息
    {
        this.msg = msg;
    }

    public Date getLoginTime() // 获取访问时间
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) // 设置访问时间
    {
        this.loginTime = loginTime;
    }
}
