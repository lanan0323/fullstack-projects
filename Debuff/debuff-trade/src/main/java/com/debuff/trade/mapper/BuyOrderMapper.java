// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.debuff.trade.domain.entity.BuyOrder;
import com.debuff.trade.domain.vo.BuyOrderVO;

/**
 * 求购单 数据层
 * 
 * @author B同学
 */
public interface BuyOrderMapper // 求购单Mapper接口
{
    /**
     * 查询某饰品的有效求购（按价格降序）
     * 
     * @param skinId 饰品ID
     * @param limit 条数限制
     * @return 求购VO集合
     */
    public List<BuyOrderVO> selectBySkin(@Param("skinId") Long skinId, @Param("limit") Integer limit); // 查询饰品求购列表

    /**
     * 查询我的求购列表
     * 
     * @param userId 用户ID
     * @return 求购VO集合
     */
    public List<BuyOrderVO> selectMyBuyOrders(Long userId); // 查询我的求购

    /**
     * 通过ID查询求购单
     * 
     * @param id 求购单ID
     * @return 求购单
     */
    public BuyOrder selectBuyOrderById(Long id); // 根据ID查询求购单

    /**
     * 新增求购单
     * 
     * @param buyOrder 求购单
     * @return 结果
     */
    public int insertBuyOrder(BuyOrder buyOrder); // 新增求购单

    /**
     * 修改求购价格（仅有效状态）
     * 
     * @param id 求购单ID
     * @param price 新价格
     * @return 结果
     */
    public int updatePrice(@Param("id") Long id, @Param("price") java.math.BigDecimal price); // 修改求购价格

    /**
     * 取消求购（仅有效状态）
     * 
     * @param id 求购单ID
     * @param userId 用户ID
     * @return 结果
     */
    public int cancelBuyOrder(@Param("id") Long id, @Param("userId") Long userId); // 取消求购

    /**
     * 接受求购（卖家出售，仅有效状态）
     * 
     * @param id 求购单ID
     * @return 结果
     */
    public int acceptBuyOrder(@Param("id") Long id); // 接受求购（卖家成交）
}
