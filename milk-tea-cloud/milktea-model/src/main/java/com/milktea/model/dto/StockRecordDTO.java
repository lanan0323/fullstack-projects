package com.milktea.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockRecordDTO {
    private Long materialId;
    private Integer type;
    private BigDecimal quantity;
    private String remark;
}
