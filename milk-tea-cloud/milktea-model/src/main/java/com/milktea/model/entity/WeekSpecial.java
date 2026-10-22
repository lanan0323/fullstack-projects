package com.milktea.model.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 每周9.9特价活动配置 (mt_user.week_special)
 * 注: 活动价由管理端配置, mall前端读此配置展示活动价; 下单按前端快照价, 金额全链路一致
 */
@Data
public class WeekSpecial {

    private Long id;

    /** 商品ID(对应mt_product.product.id) */
    private Long productId;

    /** 活动价(如9.9) */
    private BigDecimal specialPrice;

    /** 1启用 0停用 */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
