// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 数据看板 统计查询
 * 
 * @author debuff
 */
public interface AdminStatsMapper // 管理端统计Mapper接口
{
    /**
     * 总览统计（用户数/饰品数/在售数/订单数/成交额/今日成交/钱包余额）
     * 
     * @return 统计结果 Map
     */
    public Map<String, Object> selectOverview(); // 查询总览数据

    /**
     * 近 N 天每日订单数与成交额
     * 
     * @param days 天数
     * @return [{day, orderCount, amount}]
     */
    public List<Map<String, Object>> selectOrderTrend(@Param("days") int days); // 查询订单趋势

    /**
     * 成交金额 TOP N 饰品
     * 
     * @param limit 条数
     * @return [{skinName, amount, orderCount}]
     */
    public List<Map<String, Object>> selectTopSkins(@Param("limit") int limit); // 查询TOP饰品

    /**
     * 订单状态分布
     * 
     * @return [{status, count}]
     */
    public List<Map<String, Object>> selectStatusDist(); // 查询订单状态分布
}
