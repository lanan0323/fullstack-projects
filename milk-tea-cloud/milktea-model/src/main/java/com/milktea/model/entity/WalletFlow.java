package com.milktea.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包流水(暂放user模块, model模块同步后迁移)
 * type: 1充值 2消费 3退款
 */
@Data
public class WalletFlow {
    private Long id;
    private Long userId;
    private Integer type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String orderNo;
    private LocalDateTime createTime;
}
