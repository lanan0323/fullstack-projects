// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.math.BigDecimal;
import com.debuff.trade.domain.Listing;

/**
 * 在售饰品Service接口（用户端上架/下架）
 * 
 * @author debuff
 */
public interface IListingService // 在售饰品服务接口
{
    /**
     * 上架：校验库存归属与状态，创建在售记录并置库存为在售
     * 
     * @param userId 当前用户ID
     * @param inventoryId 库存ID
     * @param price 售价
     * @return 在售记录
     */
    public Listing createListing(Long userId, Long inventoryId, BigDecimal price); // 创建上架记录

    /**
     * 下架：在售状态置为已下架，库存回到在库
     * 
     * @param userId 当前用户ID
     * @param inventoryId 库存ID（每个库存最多一条在售记录，按库存定位在售记录）
     */
    public void offShelf(Long userId, Long inventoryId); // 下架操作
}
