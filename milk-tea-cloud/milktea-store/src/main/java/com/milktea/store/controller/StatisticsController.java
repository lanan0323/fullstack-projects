package com.milktea.store.controller;

import com.milktea.common.result.Result;
import com.milktea.model.vo.StatisticsVO;
import com.milktea.store.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计 Controller
 * 负责人: 成员D
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /** 工作台聚合: 今日订单数/营业额/在售商品/库存预警 */
    @GetMapping("/dashboard")
    public Result<StatisticsVO> dashboard() {
        return Result.success(statisticsService.dashboard());
    }

    /** 营业额趋势(近 N 日) */
    @GetMapping("/revenue")
    public Result<List<StatisticsVO.RevenueItem>> revenue(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(statisticsService.revenueTrend(days));
    }

    /** 商品销量 TOP N */
    @GetMapping("/sales-rank")
    public Result<List<StatisticsVO.SalesRankItem>> salesRank(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(statisticsService.salesRank(limit));
    }
}
