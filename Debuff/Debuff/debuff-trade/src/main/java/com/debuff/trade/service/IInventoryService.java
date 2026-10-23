// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.util.List;
import com.debuff.trade.domain.entity.Inventory;
import com.debuff.trade.domain.vo.InventoryVO;

/**
 * 用户库存 服务层
 * 
 * @author B同学
 */
public interface IInventoryService // 库存服务接口
{
    /**
     * 查询我的库存列表
     * 
     * @param userId 用户ID
     * @param weaponTypeId 武器类型ID
     * @param qualityId 品质ID
     * @param status 状态
     * @param keyword 关键词
     * @return 库存VO集合
     */
    public List<InventoryVO> selectInventoryList(Long userId, Long weaponTypeId, Long qualityId, // 查询库存列表
            Integer status, String keyword);

    /**
     * 查询库存详情（含饰品关联信息）
     * 
     * @param id 库存ID
     * @return 库存VO
     */
    public InventoryVO selectInventoryDetail(Long id); // 查询库存详情

    /**
     * 通过ID查询库存实体（用于归属校验）
     * 
     * @param id 库存ID
     * @return 库存实体
     */
    public Inventory selectInventoryById(Long id); // 查询库存实体

    /**
     * 修改库存信息（贴纸/检视链接/磨损度等）
     * 
     * @param inventory 库存
     * @return 结果
     */
    public int updateInventory(Inventory inventory); // 修改库存

    /**
     * 修改库存状态（供交易模块调用）
     * 0在库 -> 1在售（上架）；1在售 -> 0在库（下架）；0在库 -> 2冻结（下单）
     * 
     * @param inventoryId 库存ID
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @return 结果（受影响行数，0表示原状态不匹配）
     */
    public int updateStatus(Long inventoryId, Integer fromStatus, Integer toStatus); // 条件更新库存状态

    /**
     * 新增库存（供交易模块在订单完成时给买家生成饰品）
     * 自动根据磨损度生成磨损名称，默认来源purchase、状态0在库
     * 
     * @param inventory 库存
     * @return 结果
     */
    public int addInventory(Inventory inventory); // 新增库存

    /**
     * 删除库存（供交易模块在订单完成后清理卖家已售出的库存）
     * 
     * @param inventoryId 库存ID
     * @return 结果
     */
    public int deleteInventoryById(Long inventoryId); // 删除库存

    /**
     * 管理员查询库存列表（可查看所有用户库存）
     * 
     * @param userId 用户ID（可选）
     * @param skinId 饰品ID（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @return 库存VO集合
     */
    public List<InventoryVO> selectAdminInventoryList(Long userId, Long skinId, Integer status, String keyword); // 管理员查询库存
}
