// 包声明
package com.debuff.common.utils;

// 导入PageHelper分页和SQL工具类
import com.github.pagehelper.PageHelper;
import com.debuff.common.core.page.PageDomain;
import com.debuff.common.core.page.TableSupport;
import com.debuff.common.utils.sql.SqlUtil;

/**
 * 分页工具类
 * 
 * @author ruoyi
 */
public class PageUtils extends PageHelper
{
    /**
     * 设置请求分页数据
     */
    public static void startPage() // 从请求中提取分页参数并设置PageHelper
    {
        PageDomain pageDomain = TableSupport.buildPageRequest(); // 从HTTP请求获取分页参数
        Integer pageNum = pageDomain.getPageNum(); // 页码
        Integer pageSize = pageDomain.getPageSize(); // 每页大小
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy()); // 排序字段(防SQL注入)
        Boolean reasonable = pageDomain.getReasonable(); // 合理化参数
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable); // 启动分页
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() // 清理ThreadLocal中的分页参数
    {
        PageHelper.clearPage();
    }
}
