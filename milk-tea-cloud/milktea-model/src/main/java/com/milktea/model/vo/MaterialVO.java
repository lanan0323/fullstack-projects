package com.milktea.model.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MaterialVO {
    private Long id;
    private String name;
    private String unit;
    private BigDecimal currentStock;
    private BigDecimal minThreshold;
    private Integer status;
    private String stockStatus;
}
