package com.milktea.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 下单扣减原料入参项
 */
@Data
public class DeductItem {
    private Long productId;
    private BigDecimal quantity;
}
