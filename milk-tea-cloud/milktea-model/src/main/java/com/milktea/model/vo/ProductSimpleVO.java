package com.milktea.model.vo;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class ProductSimpleVO {
    private Long id;
    private String productName;
    private String imageUrl;
    private String description;
    private BigDecimal price;
    private Integer status;
    private String categoryName;
    private Integer sort;
}
