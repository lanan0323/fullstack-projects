// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.enums.BusinessType;
import com.debuff.trade.domain.Favorite;
import com.debuff.trade.service.IFavoriteService;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.common.core.page.TableDataInfo;

/**
 * 收藏Controller
 * 
 * @author debuff
 * @date 2026-08-26
 */
@RestController // 标记为REST控制器
@RequestMapping("/trade/favorite") // 设置收藏管理请求路径前缀
public class FavoriteController extends BaseController // 继承基础控制器
{
    @Autowired // 自动注入收藏服务
    private IFavoriteService favoriteService;

    /**
     * 查询收藏列表
     */
    @PreAuthorize("@ss.hasPermi('trade:favorite:list')") // 权限校验：需要收藏列表权限
    @GetMapping("/list") // GET请求映射到收藏列表接口
    public TableDataInfo list(Favorite favorite) // 分页查询收藏列表
    {
        startPage(); // 启动PageHelper分页
        List<Favorite> list = favoriteService.selectFavoriteList(favorite); // 调用服务层查询收藏列表
        return getDataTable(list); // 封装为分页表格数据返回
    }

    /**
     * 导出收藏列表
     */
    @PreAuthorize("@ss.hasPermi('trade:favorite:export')") // 权限校验：需要收藏导出权限
    @Log(title = "收藏", businessType = BusinessType.EXPORT) // 记录操作日志，类型为导出
    @PostMapping("/export") // POST请求映射到导出接口
    public void export(HttpServletResponse response, Favorite favorite) // 导出收藏数据为Excel
    {
        List<Favorite> list = favoriteService.selectFavoriteList(favorite); // 查询全部收藏数据
        ExcelUtil<Favorite> util = new ExcelUtil<Favorite>(Favorite.class); // 创建Excel工具实例
        util.exportExcel(response, list, "收藏数据"); // 将数据导出为Excel并写入响应流
    }

    /**
     * 获取收藏详细信息
     */
    @PreAuthorize("@ss.hasPermi('trade:favorite:query')") // 权限校验：需要收藏查询权限
    @GetMapping(value = "/{id}") // GET请求，路径参数为收藏ID
    public AjaxResult getInfo(@PathVariable("id") Long id) // 根据ID获取收藏详情
    {
        return success(favoriteService.selectFavoriteById(id)); // 返回收藏详细信息
    }

    /**
     * 新增收藏
     */
    @PreAuthorize("@ss.hasPermi('trade:favorite:add')") // 权限校验：需要收藏新增权限
    @Log(title = "收藏", businessType = BusinessType.INSERT) // 记录操作日志，类型为新增
    @PostMapping // POST请求处理新增
    public AjaxResult add(@RequestBody Favorite favorite) // 接收JSON格式的新增收藏数据
    {
        return toAjax(favoriteService.insertFavorite(favorite)); // 调用服务层新增收藏并返回结果
    }

    /**
     * 修改收藏
     */
    @PreAuthorize("@ss.hasPermi('trade:favorite:edit')") // 权限校验：需要收藏编辑权限
    @Log(title = "收藏", businessType = BusinessType.UPDATE) // 记录操作日志，类型为更新
    @PutMapping // PUT请求处理修改
    public AjaxResult edit(@RequestBody Favorite favorite) // 接收JSON格式的修改收藏数据
    {
        return toAjax(favoriteService.updateFavorite(favorite)); // 调用服务层更新收藏并返回结果
    }

    /**
     * 删除收藏
     */
    @PreAuthorize("@ss.hasPermi('trade:favorite:remove')") // 权限校验：需要收藏删除权限
    @Log(title = "收藏", businessType = BusinessType.DELETE) // 记录操作日志，类型为删除
	@DeleteMapping("/{ids}") // DELETE请求，支持批量删除
    public AjaxResult remove(@PathVariable Long[] ids) // 接收多个收藏ID进行批量删除
    {
        return toAjax(favoriteService.deleteFavoriteByIds(ids)); // 调用服务层批量删除收藏并返回结果
    }
}
