package com.milktea.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 外卖收货地址 (mt_user.mt_address)
 * 注: 实体暂放user模块(model模块需组内同步后迁移)
 */
@Data
public class Address {

    private Long id;

    /** 所属用户ID */
    private Long userId;

    /** 收货人 */
    private String receiver;

    /** 联系电话 */
    private String phone;

    /** 收货地址 */
    private String address;

    /** 是否默认 1是 0否 */
    private Integer isDefault;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
