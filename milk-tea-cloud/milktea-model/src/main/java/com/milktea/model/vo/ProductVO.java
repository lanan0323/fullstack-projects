package com.milktea.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String productName;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer status;
    private Integer sort;
    private List<SpecOptionVO> specs;

    @Data
    public static class SpecOptionVO {
        private Long specTypeId;
        private String specTypeName;
        private Long specOptionId;
        private String specOptionName;
        private BigDecimal extraPrice;
    }
}
