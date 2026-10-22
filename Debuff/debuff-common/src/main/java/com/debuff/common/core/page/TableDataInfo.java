// 包声明：分页数据所在包
package com.debuff.common.core.page;

// 导入Serializable接口
import java.io.Serializable;
// 导入List接口
import java.util.List;

/**
 * 表格分页数据对象
 * 
 * @author ruoyi
 */
public class TableDataInfo implements Serializable // 分页数据封装，用于返回给前端表格
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 总记录数 */
    private long total; // 总记录数

    /** 列表数据 */
    private List<?> rows; // 当前页数据列表

    /** 消息状态码 */
    private int code; // 响应状态码

    /** 消息内容 */
    private String msg; // 响应消息

    /**
     * 表格数据对象
     */
    public TableDataInfo() // 无参构造方法
    {
    }

    /**
     * 分页
     * 
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<?> list, int total) // 构造方法，设置数据和总数
    {
        this.rows = list; // 设置数据列表
        this.total = total; // 设置总记录数
    }

    public long getTotal() { return total; } // 获取总记录数

    public void setTotal(long total) { this.total = total; } // 设置总记录数

    public List<?> getRows() { return rows; } // 获取数据列表

    public void setRows(List<?> rows) { this.rows = rows; } // 设置数据列表

    public int getCode() { return code; } // 获取状态码

    public void setCode(int code) { this.code = code; } // 设置状态码

    public String getMsg() { return msg; } // 获取消息

    public void setMsg(String msg) { this.msg = msg; } // 设置消息
}
