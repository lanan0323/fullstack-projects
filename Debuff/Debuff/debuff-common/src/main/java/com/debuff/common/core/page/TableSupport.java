// 包声明：分页数据所在包
package com.debuff.common.core.page;

// 导入类型转换工具
import com.debuff.common.core.text.Convert;
// 导入Servlet工具类
import com.debuff.common.utils.ServletUtils;

/**
 * 表格数据处理
 * 
 * @author ruoyi
 */
public class TableSupport // 表格分页参数处理工具类
{
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum"; // 页码参数名

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize"; // 每页大小参数名

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn"; // 排序字段参数名

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc"; // 排序方向参数名

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable"; // 合理化参数名

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain() // 从请求中获取分页参数并封装为PageDomain
    {
        PageDomain pageDomain = new PageDomain(); // 创建分页参数对象
        pageDomain.setPageNum(Convert.toInt(ServletUtils.getParameter(PAGE_NUM), 1)); // 获取页码，默认第1页
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10)); // 获取每页大小，默认10条
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN)); // 获取排序字段
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC)); // 获取排序方向
        pageDomain.setReasonable(ServletUtils.getParameterToBool(REASONABLE)); // 获取合理化参数
        return pageDomain; // 返回分页参数对象
    }

    public static PageDomain buildPageRequest() // 构建分页请求（别名方法）
    {
        return getPageDomain(); // 调用getPageDomain
    }
}
