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
import com.debuff.system.domain.SysConfig;
import com.debuff.system.service.ISysConfigService;

/**
 * 参数配置 信息操作处理
 * 
 * @author ruoyi
 */
// 标记为REST控制器，映射路径为/system/config
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController
{
    // 注入参数配置服务
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    // 需要system:config:list权限
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config)
    {
        // 开启分页
        startPage();
        // 查询参数配置列表
        List<SysConfig> list = configService.selectConfigList(config);
        // 返回分页数据
        return getDataTable(list);
    }

    // 记录导出日志，需要system:config:export权限
    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config)
    {
        // 查询参数配置列表
        List<SysConfig> list = configService.selectConfigList(config);
        // 使用Excel工具导出参数数据
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    // 需要system:config:query权限
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        // 根据ID查询参数配置详情
        return success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey)
    {
        // 根据键名查询对应的参数值
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    // 需要system:config:add权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysConfig config)
    {
        // 校验参数键名是否唯一
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        // 设置创建人
        config.setCreateBy(getUsername());
        // 执行新增操作
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    // 需要system:config:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config)
    {
        // 校验参数键名是否唯一
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        // 设置修改人
        config.setUpdateBy(getUsername());
        // 执行修改操作
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    // 需要system:config:remove权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        // 批量删除参数配置
        configService.deleteConfigByIds(configIds);
        return success();
    }

    /**
     * 刷新参数缓存
     */
    // 需要system:config:remove权限，记录清除缓存日志
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        // 重置参数配置缓存
        configService.resetConfigCache();
        return success();
    }
}
