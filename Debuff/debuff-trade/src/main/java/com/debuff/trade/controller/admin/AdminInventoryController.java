// 包声明
package com.debuff.trade.controller.admin;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.annotation.Log;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.enums.BusinessType;
import com.debuff.trade.domain.entity.Inventory;
import com.debuff.trade.domain.vo.InventoryVO;
import com.debuff.trade.service.IInventoryService;

/**
 * 库存管理 后台接口
 * 
 * @author B同学
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/admin/inventory") // 管理端库存API路径前缀
public class AdminInventoryController extends BaseController // 管理端库存控制器
{
    @Autowired // 自动注入库存服务
    private IInventoryService inventoryService;

    /**
     * 库存列表（管理员可查看所有库存）
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:list')") // 权限校验：库存列表
    @GetMapping("/list") // GET /api/admin/inventory/list
    public TableDataInfo list(@RequestParam(required = false) Long userId, // 用户ID筛选（可选）
            @RequestParam(required = false) Long skinId, // 饰品ID筛选（可选）
            @RequestParam(required = false) Integer status, // 状态筛选（可选）
            @RequestParam(required = false) String keyword) // 关键词搜索（可选）
    {
        startPage(); // 启动PageHelper分页
        List<InventoryVO> list = inventoryService.selectAdminInventoryList(userId, skinId, status, keyword); // 查询库存列表
        return getDataTable(list); // 返回分页数据
    }

    /**
     * 更新库存信息（管理员可修改磨损度等）
     */
    @PreAuthorize("@ss.hasPermi('admin:skin:edit')") // 权限校验：库存编辑
    @Log(title = "库存管理", businessType = BusinessType.UPDATE) // 操作日志记录
    @PutMapping("/{id}") // PUT /api/admin/inventory/{id}
    public AjaxResult edit(@PathVariable Long id, @RequestBody Inventory inventory) // 接收路径ID和请求体
    {
        Inventory dbInventory = inventoryService.selectInventoryById(id); // 查询数据库中的库存记录
        if (dbInventory == null) // 记录不存在
        {
            return error("库存记录不存在"); // 返回错误
        }
        inventory.setId(id); // 设置ID（防止篡改）
        inventory.setUserId(dbInventory.getUserId()); // 保留原用户ID（防止篡改）
        inventory.setSkinId(dbInventory.getSkinId()); // 保留原饰品ID（防止篡改）
        inventory.setStatus(dbInventory.getStatus()); // 保留原状态（防止篡改）
        return toAjax(inventoryService.updateInventory(inventory)); // 执行更新并返回结果
    }
}
