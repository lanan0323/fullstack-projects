// 包声明
package com.debuff.web.controller.system;

// 导入集合、Servlet、权限、验证、Excel和日志等依赖
import java.util.ArrayList;
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
import com.debuff.common.core.domain.entity.SysDictData;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.system.service.ISysDictDataService;
import com.debuff.system.service.ISysDictTypeService;

/**
 * 数据字典信息
 * 
 * @author ruoyi
 */
// 标记为REST控制器，映射路径为/system/dict/data
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController
{
    // 注入字典数据服务
    @Autowired
    private ISysDictDataService dictDataService;

    // 注入字典类型服务
    @Autowired
    private ISysDictTypeService dictTypeService;

    // 需要system:dict:list权限
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData)
    {
        // 开启分页
        startPage();
        // 查询字典数据列表
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        // 返回分页数据
        return getDataTable(list);
    }

    // 记录导出日志，需要system:dict:export权限
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictData dictData)
    {
        // 查询字典数据列表
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        // 使用Excel工具导出字典数据
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * 查询字典数据详细
     */
    // 需要system:dict:query权限
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode)
    {
        // 根据编码查询字典数据详情
        return success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType)
    {
        // 根据字典类型查询对应的数据列表
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        // 如果查询结果为空，返回空列表避免null
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return success(data);
    }

    /**
     * 新增字典类型
     */
    // 需要system:dict:add权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict)
    {
        // 设置创建人
        dict.setCreateBy(getUsername());
        // 执行新增操作
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */
    // 需要system:dict:edit权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictData dict)
    {
        // 设置修改人
        dict.setUpdateBy(getUsername());
        // 执行修改操作
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     */
    // 需要system:dict:remove权限，记录操作日志
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes)
    {
        // 批量删除字典数据
        dictDataService.deleteDictDataByIds(dictCodes);
        return success();
    }
}
