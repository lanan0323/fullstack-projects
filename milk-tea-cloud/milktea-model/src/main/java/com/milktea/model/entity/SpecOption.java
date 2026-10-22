package com.milktea.model.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SpecOption {
    private Long id;
    private Long specTypeId;
    private String optionName;
    private BigDecimal extraPrice;
    private Integer sort;
    private Integer status;
}
