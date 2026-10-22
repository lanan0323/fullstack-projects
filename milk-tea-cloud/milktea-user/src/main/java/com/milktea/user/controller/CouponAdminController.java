package com.milktea.user.controller;

import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.PageResult;
import com.milktea.common.result.Result;
import com.milktea.model.entity.User;
import com.milktea.model.entity.Coupon;
import com.milktea.model.entity.UserCoupon;
import com.milktea.user.mapper.UserMapper;
import com.milktea.user.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠券管理(管理端): 挂 /coupon 下, 走网关 /api/coupon/** 路由, 需登录token
 */
@RestController
@RequestMapping("/coupon")
public class CouponAdminController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserMapper userMapper;

    /** 券模板分页 */
    @GetMapping("/page")
    public Result<PageResult<Coupon>> page(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(couponService.page(name, status, pageNum, pageSize));
    }

    /** 新增券模板 */
    @PostMapping
    public Result<Void> add(@RequestBody Coupon coupon) {
        couponService.add(coupon);
        return Result.success();
    }

    /** 编辑券模板 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        couponService.update(coupon);
        return Result.success();
    }

    /** 上下架(1上架/0下架) */
    @PutMapping("/{id}/status/{status}")
    public Result<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        couponService.updateStatus(id, status);
        return Result.success();
    }

    /** 删除(仅下架且无领取记录可删) */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return Result.success();
    }

    /** 定向发放: 手机号或用户ID二选一 + 券ID */
    @PostMapping("/issue")
    public Result<Void> issue(@RequestParam Long couponId,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) Long userId) {
        Long uid = userId;
        if (uid == null && phone != null && !phone.isBlank()) {
            User user = userMapper.findByPhone(phone);
            if (user == null) {
                throw new BusinessException("未找到手机号为 " + phone + " 的用户");
            }
            uid = user.getId();
        }
        if (uid == null) {
            throw new BusinessException("请填写用户手机号或用户ID");
        }
        couponService.issue(uid, couponId);
        return Result.success();
    }

    /** 领取/发放记录(联查用户昵称手机号) */
    @GetMapping("/records")
    public Result<PageResult<UserCoupon>> records(@RequestParam(required = false) Long couponId,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(couponService.records(couponId, pageNum, pageSize));
    }
}
