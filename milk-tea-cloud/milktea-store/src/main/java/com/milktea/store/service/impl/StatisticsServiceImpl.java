package com.milktea.store.service.impl;

import com.milktea.model.vo.StatisticsVO;
import com.milktea.store.mapper.MaterialMapper;
import com.milktea.store.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 统计服务实现
 *
 * 跨库统计(方案① Feign 接入):
 * 订单/营业额/销量 → OrderStatsClient 调 order 服务 /order/stats/internal;
 * 在售商品数 → ProductClient 调 product 服务 /product/list 过滤在售;
 * 库存预警 → 本库原料表直接统计。
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private com.milktea.api.client.OrderStatsClient orderStatsClient;

    @Autowired
    private com.milktea.api.client.ProductClient productClient;

    @Override
    public StatisticsVO dashboard() {
        StatisticsVO vo = new StatisticsVO();
        // 订单/营业额: Feign 调 order 服务(降级时返回零值)
        try {
            com.milktea.common.result.Result<java.util.Map<String, Object>> r = orderStatsClient.stats(7, 5);
            if (r != null && r.getCode() != null && r.getCode() == 200 && r.getData() != null) {
                java.util.Map<String, Object> d = r.getData();
                vo.setTodayOrderCount(((Number) d.getOrDefault("todayOrderCount", 0)).intValue());
                Object rev = d.get("todayRevenue");
                vo.setTodayRevenue(rev == null ? BigDecimal.ZERO : new BigDecimal(rev.toString()));
            }
        } catch (Exception e) {
            vo.setTodayOrderCount(0);
            vo.setTodayRevenue(BigDecimal.ZERO);
        }
        // 在售商品数: Feign 调 product 服务
        try {
            com.milktea.common.result.Result<java.util.List<com.milktea.model.entity.Product>> pr = productClient.list();
            if (pr != null && pr.getCode() != null && pr.getCode() == 200 && pr.getData() != null) {
                vo.setProductCount((int) pr.getData().stream()
                        .filter(p -> p.getStatus() == null || p.getStatus() == 1).count());
            }
        } catch (Exception e) {
            vo.setProductCount(0);
        }
        // 本库可统计
        vo.setStockWarningCount((int) materialMapper.countWarning());
        vo.setRevenueTrend(revenueTrend(7));
        vo.setSalesRank(salesRank(5));
        return vo;
    }

    @Override
    public List<StatisticsVO.RevenueItem> revenueTrend(Integer days) {
        try {
            com.milktea.common.result.Result<java.util.Map<String, Object>> r = orderStatsClient.stats(days, 5);
            if (r == null || r.getCode() == null || r.getCode() != 200 || r.getData() == null) {
                return Collections.emptyList();
            }
            List<StatisticsVO.RevenueItem> list = new java.util.ArrayList<>();
            Object trend = r.getData().get("trend");
            if (trend instanceof List) {
                for (Object o : (List<?>) trend) {
                    if (o instanceof java.util.Map) {
                        java.util.Map<?, ?> m = (java.util.Map<?, ?>) o;
                        StatisticsVO.RevenueItem item = new StatisticsVO.RevenueItem();
                        item.setDate(m.get("date") == null ? "" : m.get("date").toString());
                        item.setRevenue(m.get("revenue") == null ? BigDecimal.ZERO : new BigDecimal(m.get("revenue").toString()));
                        list.add(item);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<StatisticsVO.SalesRankItem> salesRank(Integer limit) {
        try {
            com.milktea.common.result.Result<java.util.Map<String, Object>> r = orderStatsClient.stats(7, limit == null ? 5 : limit);
            if (r == null || r.getCode() == null || r.getCode() != 200 || r.getData() == null) {
                return Collections.emptyList();
            }
            List<StatisticsVO.SalesRankItem> list = new java.util.ArrayList<>();
            Object rank = r.getData().get("rank");
            if (rank instanceof List) {
                for (Object o : (List<?>) rank) {
                    if (o instanceof java.util.Map) {
                        java.util.Map<?, ?> m = (java.util.Map<?, ?>) o;
                        StatisticsVO.SalesRankItem item = new StatisticsVO.SalesRankItem();
                        item.setProductName(m.get("name") == null ? "" : m.get("name").toString());
                        Object sales = m.get("sales");
                        item.setSalesCount(sales == null ? 0 : ((Number) sales).intValue());
                        list.add(item);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
