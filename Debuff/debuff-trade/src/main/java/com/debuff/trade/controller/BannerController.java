// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Anonymous;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.trade.domain.Banner;
import com.debuff.trade.service.IBannerService;

/** 首页轮播图管理接口 */
@RestController
@RequestMapping("/trade/banner") // 请求路径前缀
public class BannerController extends BaseController
{
    @Autowired
    private IBannerService bannerService; // 轮播图服务

    /**
     * 查询轮播图列表（管理端分页）
     */
    @PreAuthorize("@ss.hasPermi('trade:banner:list')") // 权限校验
    @GetMapping("/list") // GET /trade/banner/list
    public TableDataInfo list(Banner banner)
    {
        startPage(); // 启动PageHelper分页
        List<Banner> list = bannerService.selectBannerList(banner); // 查询列表
        return getDataTable(list); // 封装分页结果
    }

    /** 用户端只读取已发布轮播图 */
    @Anonymous // 无需登录
    @GetMapping("/publicList") // GET /trade/banner/publicList
    public AjaxResult publicList()
    {
        return success(bannerService.selectPublicBanners()); // 返回已发布轮播图
    }

    /**
     * 获取轮播图详细信息
     */
    @PreAuthorize("@ss.hasPermi('trade:banner:query')") // 权限校验
    @GetMapping("/{id}") // GET /trade/banner/{id}
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(bannerService.selectBannerById(id)); // 返回轮播图详情
    }

    /**
     * 新增轮播图
     */
    @PreAuthorize("@ss.hasPermi('trade:banner:add')") // 权限校验
    @Log(title = "首页轮播图", businessType = BusinessType.INSERT) // 操作日志
    @PostMapping // POST /trade/banner
    public AjaxResult add(@RequestBody Banner banner)
    {
        return toAjax(bannerService.insertBanner(banner)); // 新增并返回结果
    }

    /**
     * 修改轮播图
     */
    @PreAuthorize("@ss.hasPermi('trade:banner:edit')") // 权限校验
    @Log(title = "首页轮播图", businessType = BusinessType.UPDATE) // 操作日志
    @PutMapping // PUT /trade/banner
    public AjaxResult edit(@RequestBody Banner banner)
    {
        return toAjax(bannerService.updateBanner(banner)); // 修改并返回结果
    }

    /**
     * 删除轮播图
     */
    @PreAuthorize("@ss.hasPermi('trade:banner:remove')") // 权限校验
    @Log(title = "首页轮播图", businessType = BusinessType.DELETE) // 操作日志
    @DeleteMapping("/{ids}") // DELETE /trade/banner/{ids}
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bannerService.deleteBannerByIds(ids)); // 批量删除并返回结果
    }
}
