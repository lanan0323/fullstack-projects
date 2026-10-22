// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import org.apache.ibatis.annotations.Param;
import com.debuff.trade.domain.Listing;

/**
 * 在售饰品Mapper接口
 * 
 * @author debuff
 * @date 2026-08-26
 */
public interface ListingMapper // 在售饰品Mapper接口
{
    /**
     * 通过ID查询在售饰品
     * 
     * @param id 主键
     * @return 在售饰品
     */
    public Listing selectListingById(Long id); // 根据ID查询在售记录

    /**
     * 条件更新状态（用于下单/取消的乐观校验）
     * 
     * @param id 主键
     * @param fromStatus 原状态
     * @param toStatus 目标状态
     * @return 受影响行数（0表示原状态不匹配）
     */
    public int updateStatus(@Param("id") Long id, @Param("fromStatus") Integer fromStatus, @Param("toStatus") Integer toStatus); // 乐观锁更新状态

    /**
     * 标记已售（0在售 -> 1已售，记录售出时间）
     * 
     * @param id 主键
     * @return 受影响行数
     */
    public int markSold(Long id); // 标记为已售

    /**
     * 新增上架记录
     * 
     * @param listing 在售饰品
     * @return 结果
     */
    public int insertListing(Listing listing); // 新增上架记录

    /**
     * 通过库存ID查询在售记录（防重复上架）
     * 
     * @param inventoryId 库存ID
     * @return 在售饰品
     */
    public Listing selectListingByInventoryId(Long inventoryId); // 按库存ID查询在售记录

    /**
     * 按饰品批量修改在售记录状态（供管理端上/下架联动：0在售 1已售 2已下架 3已取消）
     * 
     * @param skinId 饰品ID
     * @param toStatus 目标状态
     * @return 受影响行数
     */
    public int updateStatusBySkinId(@Param("skinId") Long skinId, @Param("toStatus") Integer toStatus); // 按饰品批量更新状态

    /**
     * 删除饰品下全部挂单（删除饰品定义时级联清理）
     * 
     * @param skinId 饰品ID
     * @return 受影响行数
     */
    public int deleteBySkinId(Long skinId); // 按饰品ID删除全部挂单
}
