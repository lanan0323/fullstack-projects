// 包声明
package com.debuff.web.controller.system;

// 导入集合、Servlet、权限、验证、Excel和日志等依赖
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.system.domain.SysPost;
import com.debuff.system.service.ISysPostService;

/**
 * 岗位信息操作处理
 * 
 * @author ruoyi
 */
// 标记为REST控制器，映射路径为/system/post
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController
{
    // 注入岗位服务
    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    // 需要system:post:list权限
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPost post)
    {
        // 开启分页
        startPage();
        // 查询岗位列表
        List<SysPost> list = postService.selectPostList(post);
        // 返回分页数据
        return getDataTable(list);
    }
    
    // 记录导出日志，需要system:post:export权限
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post)
    {
        // 查询岗位列表
        List<SysPost> list = postService.selectPostList(post);
        // 使用Excel工具导出岗位数据
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    // 需要system:post:query权限
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        // 根据ID查询岗位详情
        return success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    // 需要system:post:add权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysPost post)
    {
        // 校验岗位名称是否唯一
        if (!postService.checkPostNameUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        // 校验岗位编码是否唯一
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        // 设置创建人
        post.setCreateBy(getUsername());
        // 执行新增操作
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    // 需要system:post:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysPost post)
    {
        // 校验岗位名称是否唯一
        if (!postService.checkPostNameUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        // 校验岗位编码是否唯一
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        // 设置修改人
        post.setUpdateBy(getUsername());
        // 执行修改操作
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    // 需要system:post:remove权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        // 批量删除岗位
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        // 查询所有岗位
        List<SysPost> posts = postService.selectPostAll();
        return success(posts);
    }
}
