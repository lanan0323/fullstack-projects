package com.milktea.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderEvaluation {
    private Long id;
    private Long orderId;
    private Long userId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
}
