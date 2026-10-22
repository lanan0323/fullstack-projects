package com.milktea.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateDTO {
    private Long userId;
    private String remark;
    private Long couponId;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private String productName;
        private String specs;
        private BigDecimal price;
        private Integer quantity;
    }
}
