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
import com.debuff.trade.domain.WalletLog;
import com.debuff.trade.service.IWalletLogService;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.common.core.page.TableDataInfo;

/**
 * 钱包流水Controller
 * 
 * @author debuff
 * @date 2026-08-25
 */
@RestController // 标记为REST控制器
@RequestMapping("/trade/walletLog") // 设置钱包流水请求路径前缀
public class WalletLogController extends BaseController // 继承基础控制器
{
    @Autowired // 自动注入钱包流水服务
    private IWalletLogService walletLogService;

    /**
     * 查询钱包流水列表
     */
    @PreAuthorize("@ss.hasPermi('trade:walletLog:list')") // 权限校验：需要流水列表权限
    @GetMapping("/list") // GET请求映射到流水列表接口
    public TableDataInfo list(WalletLog walletLog) // 分页查询钱包流水列表
    {
        startPage(); // 启动PageHelper分页
        List<WalletLog> list = walletLogService.selectWalletLogList(walletLog); // 调用服务层查询流水列表
        return getDataTable(list); // 封装为分页表格数据返回
    }

    /**
     * 导出钱包流水列表
     */
    @PreAuthorize("@ss.hasPermi('trade:walletLog:export')") // 权限校验：需要流水导出权限
    @Log(title = "钱包流水", businessType = BusinessType.EXPORT) // 记录操作日志，类型为导出
    @PostMapping("/export") // POST请求映射到导出接口
    public void export(HttpServletResponse response, WalletLog walletLog) // 导出流水数据为Excel
    {
        List<WalletLog> list = walletLogService.selectWalletLogList(walletLog); // 查询全部流水数据
        ExcelUtil<WalletLog> util = new ExcelUtil<WalletLog>(WalletLog.class); // 创建Excel工具实例
        util.exportExcel(response, list, "钱包流水数据"); // 将数据导出为Excel并写入响应流
    }

    /**
     * 获取钱包流水详细信息
     */
    @PreAuthorize("@ss.hasPermi('trade:walletLog:query')") // 权限校验：需要流水查询权限
    @GetMapping(value = "/{id}") // GET请求，路径参数为流水ID
    public AjaxResult getInfo(@PathVariable("id") Long id) // 根据ID获取流水详情
    {
        return success(walletLogService.selectWalletLogById(id)); // 返回流水详细信息
    }

    /**
     * 新增钱包流水
     */
    @PreAuthorize("@ss.hasPermi('trade:walletLog:add')") // 权限校验：需要流水新增权限
    @Log(title = "钱包流水", businessType = BusinessType.INSERT) // 记录操作日志，类型为新增
    @PostMapping // POST请求处理新增
    public AjaxResult add(@RequestBody WalletLog walletLog) // 接收JSON格式的新增流水数据
    {
        return toAjax(walletLogService.insertWalletLog(walletLog)); // 调用服务层新增流水并返回结果
    }

    /**
     * 修改钱包流水
     */
    @PreAuthorize("@ss.hasPermi('trade:walletLog:edit')") // 权限校验：需要流水编辑权限
    @Log(title = "钱包流水", businessType = BusinessType.UPDATE) // 记录操作日志，类型为更新
    @PutMapping // PUT请求处理修改
    public AjaxResult edit(@RequestBody WalletLog walletLog) // 接收JSON格式的修改流水数据
    {
        return toAjax(walletLogService.updateWalletLog(walletLog)); // 调用服务层更新流水并返回结果
    }

    /**
     * 删除钱包流水
     */
    @PreAuthorize("@ss.hasPermi('trade:walletLog:remove')") // 权限校验：需要流水删除权限
    @Log(title = "钱包流水", businessType = BusinessType.DELETE) // 记录操作日志，类型为删除
	@DeleteMapping("/{ids}") // DELETE请求，支持批量删除
    public AjaxResult remove(@PathVariable Long[] ids) // 接收多个流水ID进行批量删除
    {
        return toAjax(walletLogService.deleteWalletLogByIds(ids)); // 调用服务层批量删除流水并返回结果
    }
}
