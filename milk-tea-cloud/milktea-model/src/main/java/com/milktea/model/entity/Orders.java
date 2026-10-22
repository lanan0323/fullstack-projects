package com.milktea.model.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Orders {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    /** 优惠金额(下单抵扣) */
    private BigDecimal discountAmount;
    /** 使用的优惠券id(mt_coupon) */
    private Long couponId;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime completeTime;
}
