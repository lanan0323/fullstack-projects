package com.milktea.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Material {
    private Long id;
    private String name;
    private String unit;
    private java.math.BigDecimal currentStock;
    private java.math.BigDecimal minThreshold;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
