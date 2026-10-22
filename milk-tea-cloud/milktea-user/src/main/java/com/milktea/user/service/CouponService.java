package com.milktea.user.service;

import com.milktea.common.result.PageResult;
import com.milktea.model.entity.Coupon;
import com.milktea.model.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    /** 可领券列表(标记当前用户是否已领) */
    List<Coupon> listOnSale();

    /** 领券 */
    void receive(Long userId, Long couponId);

    /** 我的未使用券 */
    List<UserCoupon> myUnused();

    /** 核销优惠券, 返回抵扣金额 */
    BigDecimal use(Long userId, Long couponId, BigDecimal orderAmount);

    /** 取消订单回退优惠券 */
    void restore(Long userId, Long couponId);

    // ==================== 管理端 ====================

    /** 券模板分页 */
    PageResult<Coupon> page(String name, Integer status, Integer pageNum, Integer pageSize);

    /** 新增券模板 */
    void add(Coupon coupon);

    /** 编辑券模板 */
    void update(Coupon coupon);

    /** 上下架 */
    void updateStatus(Long id, Integer status);

    /** 删除(仅下架且无领取记录) */
    void delete(Long id);

    /** 定向发放给指定用户 */
    void issue(Long userId, Long couponId);

    /** 领取/发放记录分页 */
    PageResult<UserCoupon> records(Long couponId, Integer pageNum, Integer pageSize);
}
