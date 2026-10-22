package com.milktea.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private Long categoryId;
    private String productName;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer status;
    private Integer sort;
    private List<Long> specOptionIds;
}
