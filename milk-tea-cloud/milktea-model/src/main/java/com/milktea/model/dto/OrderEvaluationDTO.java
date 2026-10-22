package com.milktea.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评价入参(模块内部 DTO, 不放入共享 model)
 */
@Data
public class OrderEvaluationDTO implements Serializable {
    private Long orderId;
    private Long userId;
    private Integer rating;
    private String content;
}
