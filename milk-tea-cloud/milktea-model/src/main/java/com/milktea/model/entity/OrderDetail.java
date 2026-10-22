package com.milktea.model.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetail {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String specs;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
