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
import com.debuff.trade.domain.Wallet;
import com.debuff.trade.service.IWalletService;
import com.debuff.common.utils.poi.ExcelUtil;
import com.debuff.common.core.page.TableDataInfo;

/**
 * 用户钱包Controller
 * 
 * @author debuff
 * @date 2026-08-25
 */
@RestController // 标记为REST控制器
@RequestMapping("/trade/wallet") // 设置用户钱包请求路径前缀
public class WalletController extends BaseController // 继承基础控制器
{
    @Autowired // 自动注入钱包服务
    private IWalletService walletService;

    /**
     * 查询用户钱包列表
     */
    @PreAuthorize("@ss.hasPermi('trade:wallet:list')") // 权限校验：需要钱包列表权限
    @GetMapping("/list") // GET请求映射到钱包列表接口
    public TableDataInfo list(Wallet wallet) // 分页查询钱包列表
    {
        startPage(); // 启动PageHelper分页
        List<Wallet> list = walletService.selectWalletList(wallet); // 调用服务层查询钱包列表
        return getDataTable(list); // 封装为分页表格数据返回
    }

    /**
     * 导出用户钱包列表
     */
    @PreAuthorize("@ss.hasPermi('trade:wallet:export')") // 权限校验：需要钱包导出权限
    @Log(title = "用户钱包", businessType = BusinessType.EXPORT) // 记录操作日志，类型为导出
    @PostMapping("/export") // POST请求映射到导出接口
    public void export(HttpServletResponse response, Wallet wallet) // 导出钱包数据为Excel
    {
        List<Wallet> list = walletService.selectWalletList(wallet); // 查询全部钱包数据
        ExcelUtil<Wallet> util = new ExcelUtil<Wallet>(Wallet.class); // 创建Excel工具实例
        util.exportExcel(response, list, "用户钱包数据"); // 将数据导出为Excel并写入响应流
    }

    /**
     * 获取用户钱包详细信息
     */
    @PreAuthorize("@ss.hasPermi('trade:wallet:query')") // 权限校验：需要钱包查询权限
    @GetMapping(value = "/{id}") // GET请求，路径参数为钱包ID
    public AjaxResult getInfo(@PathVariable("id") Long id) // 根据ID获取钱包详情
    {
        return success(walletService.selectWalletById(id)); // 返回钱包详细信息
    }

    /**
     * 新增用户钱包
     */
    @PreAuthorize("@ss.hasPermi('trade:wallet:add')") // 权限校验：需要钱包新增权限
    @Log(title = "用户钱包", businessType = BusinessType.INSERT) // 记录操作日志，类型为新增
    @PostMapping // POST请求处理新增
    public AjaxResult add(@RequestBody Wallet wallet) // 接收JSON格式的新增钱包数据
    {
        return toAjax(walletService.insertWallet(wallet)); // 调用服务层新增钱包并返回结果
    }

    /**
     * 修改用户钱包
     */
    @PreAuthorize("@ss.hasPermi('trade:wallet:edit')") // 权限校验：需要钱包编辑权限
    @Log(title = "用户钱包", businessType = BusinessType.UPDATE) // 记录操作日志，类型为更新
    @PutMapping // PUT请求处理修改
    public AjaxResult edit(@RequestBody Wallet wallet) // 接收JSON格式的修改钱包数据
    {
        return toAjax(walletService.updateWallet(wallet)); // 调用服务层更新钱包并返回结果
    }

    /**
     * 删除用户钱包
     */
    @PreAuthorize("@ss.hasPermi('trade:wallet:remove')") // 权限校验：需要钱包删除权限
    @Log(title = "用户钱包", businessType = BusinessType.DELETE) // 记录操作日志，类型为删除
	@DeleteMapping("/{ids}") // DELETE请求，支持批量删除
    public AjaxResult remove(@PathVariable Long[] ids) // 接收多个钱包ID进行批量删除
    {
        return toAjax(walletService.deleteWalletByIds(ids)); // 调用服务层批量删除钱包并返回结果
    }
}
