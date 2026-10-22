package com.milktea.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsVO {
    private BigDecimal todayRevenue;
    private Integer todayOrderCount;
    private Integer productCount;
    private Integer stockWarningCount;
    private List<RevenueItem> revenueTrend;
    private List<SalesRankItem> salesRank;

    @Data
    public static class RevenueItem {
        private String date;
        private BigDecimal revenue;
    }

    @Data
    public static class SalesRankItem {
        private String productName;
        private Integer salesCount;
    }
}
