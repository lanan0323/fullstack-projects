package com.milktea.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户持券 (mt_user_coupon)
 */
@Data
public class UserCoupon {

    private Long id;

    private Long userId;

    private Long couponId;

    /** 0未使用 1已使用 2已过期 */
    private Integer status;

    private LocalDateTime receiveTime;

    private LocalDateTime expireTime;

    // ========== 关联模板字段(联查非表字段) ==========

    private String name;

    /** 1满减 2折扣 */
    private Integer type;

    /** 满减金额或折扣值 */
    private BigDecimal amount;

    /** 使用门槛 */
    private BigDecimal minAmount;

    // ========== 管理端领取记录联查字段(非表字段) ==========

    private String nickname;

    private String phone;
}
