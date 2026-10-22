// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.math.BigDecimal;
import java.util.List;
import com.debuff.trade.domain.vo.BuyOrderVO;

/**
 * 求购单 服务层
 *
 * @author B同学
 */
public interface IBuyOrderService
{
    /**
     * 发布求购（丢求购）
     *
     * @param userId 求购用户ID
     * @param skinId 饰品ID
     * @param price 求购单价
     * @return 结果（1成功；0失败）
     */
    public int createBuyOrder(Long userId, Long skinId, BigDecimal price); // 创建求购单

    /**
     * 查询某饰品的有效求购（按价格降序，前N条）
     *
     * @param skinId 饰品ID
     * @param limit 条数限制（null 表示不限制）
     * @return 求购列表
     */
    public List<BuyOrderVO> selectBySkin(Long skinId, Integer limit); // 按饰品查询求购

    /**
     * 查询我的求购列表
     *
     * @param userId 用户ID
     * @return 求购列表
     */
    public List<BuyOrderVO> selectMyBuyOrders(Long userId); // 查询当前用户的求购

    /**
     * 修改我的求购价格（仅有效状态）
     *
     * @param id 求购单ID
     * @param userId 用户ID
     * @param price 新价格
     * @return 结果（1成功；0失败）
     */
    public int updatePrice(Long id, Long userId, BigDecimal price); // 修改求购价格

    /**
     * 取消我的求购（仅有效状态）
     *
     * @param id 求购单ID
     * @param userId 用户ID
     * @return 结果（1成功；0失败）
     */
    public int cancelBuyOrder(Long id, Long userId); // 取消求购单

    /**
     * 卖家接受求购（以求购价格出售自己的饰品）
     * 校验：求购单有效、卖家拥有该饰品在库库存、买家余额充足、不能卖给自己
     * 流程：冻结买家余额 → 冻结卖家库存 → 生成交易订单 → 求购单置已完成
     *
     * @param buyOrderId 求购单ID
     * @param sellerId 卖家（当前登录用户）ID
     * @param inventoryId 卖家选定的库存ID
     * @return 生成的交易订单
     */
    public com.debuff.trade.domain.Order acceptBuyOrder(Long buyOrderId, Long sellerId, Long inventoryId); // 接受求购完成交易
}
