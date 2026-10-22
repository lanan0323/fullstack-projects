package com.milktea.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板 (mt_coupon)
 * 注: 优惠券为用户服务私有实体, 暂放本模块, 待组内同步后统一迁入 model 模块
 */
@Data
public class Coupon {

    private Long id;

    /** 券名 */
    private String name;

    /** 类型 1满减 2折扣 */
    private Integer type;

    /** 满减金额或折扣值(0.83=8.3折) */
    private BigDecimal amount;

    /** 使用门槛(满X可用, 0无门槛) */
    private BigDecimal minAmount;

    /** 剩余数量 */
    private Integer remain;

    /** 领取后有效天数 */
    private Integer validDays;

    /** 1上架 0下架 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 当前用户是否已领(非表字段) */
    private Boolean received;
}
