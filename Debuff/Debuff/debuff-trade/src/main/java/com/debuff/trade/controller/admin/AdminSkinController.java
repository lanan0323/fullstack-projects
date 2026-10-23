// 包声明
package com.debuff.trade.controller.admin;

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
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.trade.domain.entity.Skin;
import com.debuff.trade.service.ISkinService;

/**
 * 饰品定义 后台管理接口
 * 
 * @author B同学
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/admin/skin") // 设置后台管理饰品的请求路径前缀
public class AdminSkinController extends BaseController // 继承基础控制器，获得通用分页和数据返回方法
{
    @Autowired // 自动注入饰品服务
    private ISkinService skinService;

    /**
     * 饰品列表
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:list')") // 权限校验：需要饰品列表权限
    @GetMapping("/list") // GET请求映射到/list
    public TableDataInfo list(Skin skin) // 分页查询饰品列表，支持条件筛选
    {
        startPage(); // 启动PageHelper分页插件
        List<Skin> list = skinService.selectSkinList(skin); // 调用服务层查询饰品列表
        return getDataTable(list); // 将结果封装为分页表格数据返回
    }

    /**
     * 饰品详情
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:query')") // 权限校验：需要饰品查询权限
    @GetMapping("/{skinId}") // GET请求，路径参数为饰品ID
    public AjaxResult getInfo(@PathVariable Long skinId) // 根据ID获取饰品详情
    {
        return success(skinService.selectSkinById(skinId)); // 返回饰品详细信息
    }

    /**
     * 新增饰品定义
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:add')") // 权限校验：需要饰品新增权限
    @Log(title = "饰品管理", businessType = BusinessType.INSERT) // 记录操作日志，类型为新增
    @PostMapping // POST请求处理新增
    public AjaxResult add(@RequestBody Skin skin) // 接收JSON格式的新增饰品数据
    {
        return toAjax(skinService.insertSkin(skin)); // 调用服务层新增饰品并返回结果
    }

    /**
     * 编辑饰品定义
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:edit')") // 权限校验：需要饰品编辑权限
    @Log(title = "饰品管理", businessType = BusinessType.UPDATE) // 记录操作日志，类型为更新
    @PutMapping // PUT请求处理编辑
    public AjaxResult edit(@RequestBody Skin skin) // 接收JSON格式的编辑饰品数据
    {
        return toAjax(skinService.updateSkin(skin)); // 调用服务层更新饰品并返回结果
    }

    /**
     * 删除饰品定义
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:remove')") // 权限校验：需要饰品删除权限
    @Log(title = "饰品管理", businessType = BusinessType.DELETE) // 记录操作日志，类型为删除
    @DeleteMapping("/{skinIds}") // DELETE请求，支持批量删除
    public AjaxResult remove(@PathVariable Long[] skinIds) // 接收多个饰品ID进行批量删除
    {
        return toAjax(skinService.deleteSkinByIds(skinIds)); // 调用服务层批量删除饰品并返回结果
    }

    /**
     * 上架/下架
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:edit')") // 权限校验：需要饰品编辑权限
    @Log(title = "饰品管理", businessType = BusinessType.UPDATE) // 记录操作日志，类型为更新
    @PutMapping("/changeStatus") // PUT请求映射到状态变更接口
    public AjaxResult changeStatus(@RequestBody Skin skin) // 接收饰品对象，修改其上下架状态
    {
        return toAjax(skinService.updateSkinStatus(skin)); // 调用服务层更新饰品状态并返回结果
    }
}
