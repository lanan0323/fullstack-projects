package com.milktea.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分账户(暂放user模块, model模块同步后迁移)
 */
@Data
public class PointsAccount {
    private Long id;
    private Long userId;
    private Long points;
    private LocalDateTime updateTime;
}
