// 包声明
package com.debuff.common.core.controller;

// 导入日期编辑器、日志、分页、安全、SQL工具等类
import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.debuff.common.constant.HttpStatus;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.core.page.PageDomain;

import com.debuff.common.core.page.TableSupport;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.PageUtils;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.sql.SqlUtil;

// Web层通用数据处理的基类控制器
public class BaseController
{
    // 日志记录器
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 注册日期类型转换器，将前端日期字符串自动转为Date
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            // 将文本转为Date类型
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    // 设置请求分页参数（从请求中获取pageNum和pageSize）
    protected void startPage()
    {
        PageUtils.startPage();
    }

    // 设置请求排序参数
    protected void startOrderBy()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest(); // 构建分页请求对象
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy())) // 有排序字段
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy()); // SQL注入过滤
            PageHelper.orderBy(orderBy); // 设置排序
        }
    }

    // 清理分页的线程变量（防止内存泄漏）
    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    // 将列表数据封装为分页响应对象
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected com.debuff.common.core.page.TableDataInfo getDataTable(List<?> list)
    {
        com.debuff.common.core.page.TableDataInfo rspData = new com.debuff.common.core.page.TableDataInfo(); // 创建响应对象
        rspData.setCode(HttpStatus.SUCCESS); // 设置成功状态码
        rspData.setMsg("查询成功"); // 设置提示消息
        rspData.setRows(list); // 设置数据列表
        rspData.setTotal(new PageInfo(list).getTotal()); // 设置总记录数
        return rspData;
    }

    // 返回成功响应（无消息）
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    // 返回失败响应（无消息）
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    // 返回成功响应（带消息）
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }
    
    // 返回成功响应（带数据）
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }

    // 返回失败响应（带消息）
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    // 返回警告响应（带消息）
    public AjaxResult warn(String message)
    {
        return AjaxResult.warn(message);
    }

    // 根据影响行数返回操作结果
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    // 根据布尔结果返回操作结果
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    // 页面重定向跳转
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    // 获取当前登录用户信息
    public LoginUser getLoginUser()
    {
        return SecurityUtils.getLoginUser();
    }

    // 获取当前登录用户ID
    public Long getUserId()
    {
        return getLoginUser().getUserId();
    }

    // 获取当前登录用户所属部门ID
    public Long getDeptId()
    {
        return getLoginUser().getDeptId();
    }

    // 获取当前登录用户名
    public String getUsername()
    {
        return getLoginUser().getUsername();
    }
}