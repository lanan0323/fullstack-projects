package com.milktea.model.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockRecord {
    private Long id;
    private Long materialId;
    private String materialName;
    private Integer type;
    private BigDecimal quantity;
    private BigDecimal beforeStock;
    private BigDecimal afterStock;
    private String operator;
    private String remark;
    private LocalDateTime createTime;
}
