package com.milktea.store.service;

import com.milktea.model.vo.StatisticsVO;

import java.util.List;

/**
 * 统计服务接口
 * 负责人: 成员D
 */
public interface StatisticsService {

    /** 工作台聚合: 今日订单/营业额/在售商品/库存预警 */
    StatisticsVO dashboard();

    /** 营业额趋势(近 N 日) */
    List<StatisticsVO.RevenueItem> revenueTrend(Integer days);

    /** 商品销量 TOP N */
    List<StatisticsVO.SalesRankItem> salesRank(Integer limit);
}
