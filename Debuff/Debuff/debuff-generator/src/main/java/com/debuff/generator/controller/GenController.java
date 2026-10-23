// 包声明
package com.debuff.generator.controller;

// 导入所需类
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
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
import com.debuff.common.core.text.Convert;
import com.debuff.common.enums.BusinessType;
import com.debuff.generator.domain.GenTable;
import com.debuff.generator.domain.GenTableColumn;
import com.debuff.generator.service.IGenTableColumnService;
import com.debuff.generator.service.IGenTableService;

/**
 * 代码生成 操作处理
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/tool/gen") // 请求路径前缀
public class GenController extends BaseController
{
    @Autowired // 自动注入代码生成表服务
    private IGenTableService genTableService;

    @Autowired // 自动注入代码生成列服务
    private IGenTableColumnService genTableColumnService;

    /**
     * 查询代码生成列表
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:list')") // 权限校验：代码生成列表
    @GetMapping("/list") // GET请求
    public TableDataInfo genList(GenTable genTable)
    {
        startPage(); // 设置分页参数
        List<GenTable> list = genTableService.selectGenTableList(genTable); // 查询代码生成表列表
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 修改代码生成业务
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:query')") // 权限校验：代码生成查询
    @GetMapping(value = "/{tableId}") // GET请求，路径带ID
    public AjaxResult getInfo(@PathVariable Long tableId)
    {
        GenTable table = genTableService.selectGenTableById(tableId); // 查询表信息
        List<GenTable> tables = genTableService.selectGenTableAll(); // 查询所有表
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId); // 查询列信息
        Map<String, Object> map = new HashMap<String, Object>(); // 封装返回数据
        map.put("info", table); // 表信息
        map.put("rows", list); // 列信息
        map.put("tables", tables); // 所有表
        return success(map);
    }

    /**
     * 查询数据库列表
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:list')") // 权限校验：代码生成列表
    @GetMapping("/db/list") // GET请求
    public TableDataInfo dataList(GenTable genTable)
    {
        startPage(); // 设置分页参数
        List<GenTable> list = genTableService.selectDbTableList(genTable); // 查询数据库中的表列表
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 查询数据表字段列表
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:list')") // 权限校验：代码生成列表
    @GetMapping(value = "/column/{tableId}") // GET请求
    public TableDataInfo columnList(Long tableId)
    {
        TableDataInfo dataInfo = new TableDataInfo(); // 创建返回数据对象
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId); // 查询列信息
        dataInfo.setRows(list); // 设置行数据
        dataInfo.setTotal(list.size()); // 设置总数
        return dataInfo;
    }

    /**
     * 导入表结构（保存）
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:import')") // 权限校验：代码生成导入
    @Log(title = "代码生成", businessType = BusinessType.IMPORT) // 记录操作日志
    @PostMapping("/importTable") // POST导入请求
    public AjaxResult importTableSave(String tables)
    {
        String[] tableNames = Convert.toStrArray(tables); // 将逗号分隔的表名转为数组
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames); // 从数据库查询表结构
        genTableService.importGenTable(tableList); // 导入到代码生成表
        return success();
    }

    /**
     * 修改保存代码生成业务
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:edit')") // 权限校验：代码生成编辑
    @Log(title = "代码生成", businessType = BusinessType.UPDATE) // 记录操作日志
    @PutMapping // PUT请求
    public AjaxResult editSave(@Validated @RequestBody GenTable genTable)
    {
        genTableService.validateEdit(genTable); // 校验编辑参数
        genTableService.updateGenTable(genTable); // 更新代码生成表配置
        return success();
    }

    /**
     * 删除代码生成
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:remove')") // 权限校验：代码生成删除
    @Log(title = "代码生成", businessType = BusinessType.DELETE) // 记录操作日志
    @DeleteMapping("/{tableIds}") // DELETE请求，支持批量
    public AjaxResult remove(@PathVariable Long[] tableIds)
    {
        genTableService.deleteGenTableByIds(tableIds); // 批量删除代码生成配置
        return success();
    }

    /**
     * 预览代码
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:preview')") // 权限校验：代码预览
    @GetMapping("/preview/{tableId}") // GET预览请求
    public AjaxResult preview(@PathVariable("tableId") Long tableId) throws IOException
    {
        Map<String, String> dataMap = genTableService.previewCode(tableId); // 生成预览代码
        return success(dataMap); // 返回代码预览数据
    }

    /**
     * 生成代码（下载方式）
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:code')") // 权限校验：代码生成
    @Log(title = "代码生成", businessType = BusinessType.GENCODE) // 记录操作日志
    @GetMapping("/download/{tableName}") // GET下载请求
    public void download(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException
    {
        byte[] data = genTableService.downloadCode(tableName); // 生成代码zip数据
        genCode(response, data); // 输出到响应流
    }

    /**
     * 生成代码（自定义路径）
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:code')") // 权限校验：代码生成
    @Log(title = "代码生成", businessType = BusinessType.GENCODE) // 记录操作日志
    @GetMapping("/genCode/{tableName}") // GET生成请求
    public AjaxResult genCode(@PathVariable("tableName") String tableName)
    {
        genTableService.generatorCode(tableName); // 生成代码到自定义路径
        return success();
    }

    /**
     * 同步数据库
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:edit')") // 权限校验：代码生成编辑
    @Log(title = "代码生成", businessType = BusinessType.UPDATE) // 记录操作日志
    @GetMapping("/synchDb/{tableName}") // GET同步请求
    public AjaxResult synchDb(@PathVariable("tableName") String tableName)
    {
        genTableService.synchDb(tableName); // 同步数据库表结构到代码生成配置
        return success();
    }

    /**
     * 批量生成代码
     */
    @PreAuthorize("@ss.hasPermi('tool:gen:code')") // 权限校验：代码生成
    @Log(title = "代码生成", businessType = BusinessType.GENCODE) // 记录操作日志
    @GetMapping("/batchGenCode") // GET批量生成请求
    public void batchGenCode(HttpServletResponse response, String tables) throws IOException
    {
        String[] tableNames = Convert.toStrArray(tables); // 将逗号分隔的表名转为数组
        byte[] data = genTableService.downloadCode(tableNames); // 批量生成代码zip数据
        genCode(response, data); // 输出到响应流
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException // 将代码zip写入HTTP响应
    {
        response.reset(); // 重置响应
        response.addHeader("Access-Control-Allow-Origin", "*"); // 允许跨域
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition"); // 暴露Content-Disposition头
        response.setHeader("Content-Disposition", "attachment; filename=\"ruoyi.zip\""); // 设置下载文件名
        response.addHeader("Content-Length", "" + data.length); // 设置内容长度
        response.setContentType("application/octet-stream; charset=UTF-8"); // 设置内容类型为二进制流
        IOUtils.write(data, response.getOutputStream()); // 写入响应输出流
    }
}
