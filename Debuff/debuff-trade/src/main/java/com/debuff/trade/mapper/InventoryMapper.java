// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.debuff.trade.domain.entity.Inventory;
import com.debuff.trade.domain.vo.InventoryVO;

/**
 * 用户库存 数据层
 * 
 * @author B同学
 */
public interface InventoryMapper // 库存Mapper接口
{
    /**
     * 查询我的库存列表（含饰品关联信息）
     * 
     * @param userId 用户ID
     * @param weaponTypeId 武器类型ID
     * @param qualityId 品质ID
     * @param status 状态
     * @param keyword 关键词
     * @return 库存VO集合
     */
    public List<InventoryVO> selectInventoryList(@Param("userId") Long userId, // 查询库存列表
            @Param("weaponTypeId") Long weaponTypeId, @Param("qualityId") Long qualityId,
            @Param("status") Integer status, @Param("keyword") String keyword);

    /**
     * 通过ID查询库存详情（含饰品关联信息）
     * 
     * @param id 库存ID
     * @return 库存VO
     */
    public InventoryVO selectInventoryById(Long id); // 查询库存VO详情

    /**
     * 通过ID查询库存实体
     * 
     * @param id 库存ID
     * @return 库存实体
     */
    public Inventory selectInventoryEntityById(Long id); // 查询库存实体对象

    /**
     * 新增库存
     * 
     * @param inventory 库存
     * @return 结果
     */
    public int insertInventory(Inventory inventory); // 新增库存

    /**
     * 修改库存信息（贴纸/检视链接/磨损度等，不含状态）
     * 
     * @param inventory 库存
     * @return 结果
     */
    public int updateInventory(Inventory inventory); // 修改库存信息

    /**
     * 修改库存状态（供交易模块调用：0在库 1在售 2冻结）
     * 
     * @param inventoryId 库存ID
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @return 结果（受影响行数）
     */
    public int updateStatus(@Param("inventoryId") Long inventoryId, // 条件更新库存状态
            @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);

    /**
     * 查询指定用户某饰品的在库库存（0在库，取最早一条）
     * 
     * @param skinId 饰品ID
     * @param userId 用户ID
     * @return 库存实体
     */
    public Inventory selectInStockBySkinIdAndUser(@Param("skinId") Long skinId, @Param("userId") Long userId); // 查询在库库存

    /**
     * 按饰品批量修改库存状态（供管理端上/下架联动：0在库 1在售 2冻结）
     * 
     * @param skinId 饰品ID
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @return 受影响行数
     */
    public int updateStatusBySkinId(@Param("skinId") Long skinId, // 按饰品批量更新状态
            @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus);

    /**
     * 删除库存（订单完成后清理卖家已售库存）
     * 
     * @param inventoryId 库存ID
     * @return 结果
     */
    public int deleteInventoryById(Long inventoryId); // 删除库存

    /**
     * 删除饰品下全部库存（删除饰品定义时级联清理）
     * 
     * @param skinId 饰品ID
     * @return 结果
     */
    public int deleteBySkinId(Long skinId); // 按饰品ID删除全部库存

    /**
     * 管理员查询库存列表（可查看所有用户库存）
     * 
     * @param userId 用户ID（可选）
     * @param skinId 饰品ID（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @return 库存VO集合
     */
    public List<InventoryVO> selectAdminInventoryList(@Param("userId") Long userId, // 管理员查询库存
            @Param("skinId") Long skinId, @Param("status") Integer status, @Param("keyword") String keyword);

    /**
     * 批量更新某饰品所有库存的磨损度
     * 
     * @param skinId 饰品ID
     * @param floatValue 新的磨损度
     * @return 受影响行数
     */
    public int batchUpdateWearBySkinId(@Param("skinId") Long skinId, @Param("floatValue") java.math.BigDecimal floatValue); // 批量更新磨损度
}
