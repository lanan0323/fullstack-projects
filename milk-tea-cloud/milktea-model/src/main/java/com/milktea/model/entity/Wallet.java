package com.milktea.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户钱包(暂放user模块, model模块同步后迁移)
 */
@Data
public class Wallet {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private LocalDateTime updateTime;
}
