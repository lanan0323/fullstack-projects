package com.milktea.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    /** 优惠金额 */
    private BigDecimal discountAmount;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime completeTime;
    private List<OrderDetailVO> details;
    private OrderEvaluationVO evaluation;

    @Data
    public static class OrderDetailVO {
        private Long id;
        private Long productId;
        private String productName;
        private String specs;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }

    @Data
    public static class OrderEvaluationVO {
        private Integer rating;
        private String content;
        private LocalDateTime createTime;
    }
}
