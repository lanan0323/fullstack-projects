package com.milktea.user.controller;

import com.milktea.common.context.UserContext;
import com.milktea.common.result.Result;
import com.milktea.model.entity.Coupon;
import com.milktea.model.entity.UserCoupon;
import com.milktea.user.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券: 挂在 /user/coupon 下, 复用网关 /api/user/** 路由与鉴权
 */
@RestController
@RequestMapping("/user/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    /** 可领券列表 */
    @GetMapping("/list")
    public Result<List<Coupon>> list() {
        return Result.success(couponService.listOnSale());
    }

    /** 领券 */
    @PostMapping("/receive/{couponId}")
    public Result<Void> receive(@PathVariable Long couponId) {
        couponService.receive(UserContext.getCurrentUserId(), couponId);
        return Result.success();
    }

    /** 我的未使用券 */
    @GetMapping("/my")
    public Result<List<UserCoupon>> my() {
        return Result.success(couponService.myUnused());
    }

    /** 核销优惠券(供订单服务 Feign 调用, 返回抵扣金额) */
    @PostMapping("/use")
    public Result<BigDecimal> use(@RequestParam Long userId,
                                  @RequestParam Long couponId,
                                  @RequestParam BigDecimal orderAmount) {
        return Result.success(couponService.use(userId, couponId, orderAmount));
    }

    /** 取消订单回退优惠券(供订单服务 Feign 调用) */
    @PostMapping("/restore/{couponId}")
    public Result<Void> restore(@RequestParam Long userId, @PathVariable Long couponId) {
        couponService.restore(userId, couponId);
        return Result.success();
    }
}
