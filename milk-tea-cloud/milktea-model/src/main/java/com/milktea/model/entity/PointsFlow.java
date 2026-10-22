package com.milktea.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 积分流水(暂放user模块, model模块同步后迁移)
 * type: 1获得 2消耗
 */
@Data
public class PointsFlow {
    private Long id;
    private Long userId;
    private Integer type;
    private Long points;
    private String remark;
    private LocalDateTime createTime;
}
