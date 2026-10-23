// 包声明
package com.debuff.trade.controller;

// 导入所需类
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.page.TableDataInfo;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.trade.domain.entity.Inventory;
import com.debuff.trade.domain.vo.InventoryVO;
import com.debuff.trade.service.IInventoryService;

/**
 * 用户库存 前台接口
 * 
 * @author B同学
 */
@RestController // 标记为REST控制器
@RequestMapping("/api/inventory") // 设置库存相关请求路径前缀
public class InventoryController extends BaseController // 继承基础控制器
{
    @Autowired // 自动注入库存服务
    private IInventoryService inventoryService;

    /**
     * 我的库存列表（支持按武器类型/品质/状态筛选、关键词搜索）
     */
    @GetMapping("/list") // GET请求映射到库存列表接口
    public TableDataInfo list(@RequestParam(required = false) Long weaponTypeId, // 可选参数：武器类型ID
            @RequestParam(required = false) Long qualityId, // 可选参数：品质ID
            @RequestParam(required = false) Integer status, // 可选参数：库存状态
            @RequestParam(required = false) String keyword) // 可选参数：搜索关键词
    {
        startPage(); // 启动PageHelper分页
        Long userId = SecurityUtils.getUserId(); // 获取当前登录用户ID
        List<InventoryVO> list = inventoryService.selectInventoryList(userId, weaponTypeId, qualityId,
                status, keyword); // 根据条件查询当前用户的库存列表
        return getDataTable(list); // 封装为分页表格数据返回
    }

    /**
     * 库存详情
     */
    @GetMapping("/detail/{id}") // GET请求，路径参数为库存ID
    public AjaxResult detail(@PathVariable Long id) // 获取指定库存的详细信息
    {
        Inventory inventory = checkOwner(id); // 校验当前用户是否为该库存的所有者
        if (inventory == null) // 如果库存不存在或非本人所有
        {
            return error("库存记录不存在"); // 返回错误提示
        }
        return success(inventoryService.selectInventoryDetail(id)); // 返回库存详情数据
    }

    /**
     * 更新库存信息（如贴纸、检视链接）
     */
    @PutMapping("/update/{id}") // PUT请求，路径参数为库存ID
    public AjaxResult update(@PathVariable Long id, @RequestBody Inventory inventory) // 更新指定库存的信息
    {
        Inventory dbInventory = checkOwner(id); // 校验当前用户是否为库存所有者
        if (dbInventory == null) // 如果库存不存在或非本人所有
        {
            return error("库存记录不存在"); // 返回错误提示
        }
        if (Integer.valueOf(2).equals(dbInventory.getStatus())) // 如果库存状态为冻结(交易中)
        {
            return error("该饰品正在交易中，暂不能修改"); // 拒绝修改
        }
        // 仅允许修改本人库存，且不能篡改归属/饰品/状态字段
        inventory.setId(id); // 设置库存ID
        inventory.setUserId(dbInventory.getUserId()); // 锁定用户ID，防止篡改
        inventory.setSkinId(dbInventory.getSkinId()); // 锁定饰品ID，防止篡改
        inventory.setStatus(dbInventory.getStatus()); // 锁定状态，防止篡改
        return toAjax(inventoryService.updateInventory(inventory)); // 执行更新并返回结果
    }

    /**
     * 校验库存归属，返回库存实体
     */
    private Inventory checkOwner(Long id) // 校验指定库存是否属于当前登录用户
    {
        Inventory inventory = inventoryService.selectInventoryById(id); // 根据ID查询库存
        if (inventory == null || !inventory.getUserId().equals(SecurityUtils.getUserId())) // 如果不存在或不属于当前用户
        {
            return null; // 返回null表示校验失败
        }
        return inventory; // 返回库存实体
    }
}
