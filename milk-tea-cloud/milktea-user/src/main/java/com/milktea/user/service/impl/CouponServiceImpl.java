package com.milktea.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.milktea.common.context.UserContext;
import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.PageResult;
import com.milktea.model.entity.Coupon;
import com.milktea.model.entity.UserCoupon;
import com.milktea.user.mapper.CouponMapper;
import com.milktea.user.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<Coupon> listOnSale() {
        Long userId = UserContext.getCurrentUserId();
        List<Coupon> list = couponMapper.selectOnSale();
        for (Coupon c : list) {
            c.setReceived(couponMapper.countUnused(userId, c.getId()) > 0);
        }
        return list;
    }

    @Override
    @Transactional
    public void receive(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon != null && coupon.getName() != null && coupon.getName().startsWith("[等级]")) {
            throw new BusinessException("等级礼券不可手动领取, 达到等级自动发放");
        }
        issue(userId, couponId);
    }

    @Override
    public List<UserCoupon> myUnused() {
        return couponMapper.selectMyUnused(UserContext.getCurrentUserId());
    }

    @Override
    @Transactional
    public BigDecimal use(Long userId, Long couponId, BigDecimal orderAmount) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() == null || coupon.getStatus() != 1) {
            throw new BusinessException("优惠券不存在或已下架");
        }
        UserCoupon uc = couponMapper.selectUnusedById(userId, couponId);
        if (uc == null) {
            throw new BusinessException("该优惠券不可用或已过期");
        }
        // 使用门槛校验
        if (coupon.getMinAmount() != null && coupon.getMinAmount().compareTo(BigDecimal.ZERO) > 0
                && orderAmount.compareTo(coupon.getMinAmount()) < 0) {
            throw new BusinessException("未满" + coupon.getMinAmount().stripTrailingZeros().toPlainString()
                    + "元, 不可使用该券");
        }
        // 抵扣金额: 满减券直接减, 折扣券按折扣值计算
        BigDecimal discount;
        if (coupon.getType() != null && coupon.getType() == 2) {
            discount = orderAmount.multiply(BigDecimal.ONE.subtract(coupon.getAmount()))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getAmount();
        }
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        // 条件更新防并发重复核销
        if (couponMapper.markUsed(uc.getId()) == 0) {
            throw new BusinessException("优惠券核销失败, 请重试");
        }
        return discount;
    }

    @Override
    @Transactional
    public void restore(Long userId, Long couponId) {
        couponMapper.restore(userId, couponId);
    }

    // ==================== 管理端 ====================

    @Override
    public PageResult<Coupon> page(String name, Integer status, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageResult.of(couponMapper.selectPage(name, status));
    }

    @Override
    public void add(Coupon coupon) {
        validateTemplate(coupon);
        if (coupon.getStatus() == null) {
            coupon.setStatus(1);
        }
        couponMapper.insertCoupon(coupon);
    }

    @Override
    public void update(Coupon coupon) {
        if (coupon.getId() == null || couponMapper.selectById(coupon.getId()) == null) {
            throw new BusinessException("优惠券不存在");
        }
        validateTemplate(coupon);
        couponMapper.updateCoupon(coupon);
    }

    private void validateTemplate(Coupon coupon) {
        if (coupon.getName() == null || coupon.getName().isBlank()) {
            throw new BusinessException("请填写券名");
        }
        if (coupon.getType() == null || (coupon.getType() != 1 && coupon.getType() != 2)) {
            throw new BusinessException("券类型不合法");
        }
        if (coupon.getAmount() == null || coupon.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("面额/折扣值必须大于0");
        }
        if (coupon.getType() == 2 && coupon.getAmount().compareTo(BigDecimal.ONE) >= 0) {
            throw new BusinessException("折扣值须小于1, 如0.83表示8.3折");
        }
        if (coupon.getRemain() == null || coupon.getRemain() <= 0) {
            throw new BusinessException("发放数量必须大于0");
        }
        if (coupon.getValidDays() == null || coupon.getValidDays() <= 0) {
            throw new BusinessException("领取后有效天数必须大于0");
        }
        if (coupon.getMinAmount() == null) {
            coupon.setMinAmount(BigDecimal.ZERO);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态不合法");
        }
        if (couponMapper.selectById(id) == null) {
            throw new BusinessException("优惠券不存在");
        }
        couponMapper.updateStatus(id, status);
    }

    @Override
    public void delete(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getStatus() != null && coupon.getStatus() == 1) {
            throw new BusinessException("上架中的券不可删除, 请先下架");
        }
        if (couponMapper.countRecords(id) > 0) {
            throw new BusinessException("该券已有领取记录, 为保留数据请勿删除");
        }
        couponMapper.deleteById(id);
    }

    /** 发放与自领共用: 校验上架+防重复+条件扣减防超发+生成票券 */
    @Override
    @Transactional
    public void issue(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new BusinessException("优惠券不存在或已下架");
        }
        if (couponMapper.countUnused(userId, couponId) > 0) {
            throw new BusinessException("该用户已有未使用的同款券");
        }
        // 条件更新防超发
        if (couponMapper.decreaseRemain(couponId) == 0) {
            throw new BusinessException("券库存不足, 发放失败");
        }
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setExpireTime(LocalDateTime.now().plusDays(coupon.getValidDays()));
        couponMapper.insert(userCoupon);
    }

    @Override
    public PageResult<UserCoupon> records(Long couponId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageResult.of(couponMapper.selectRecords(couponId));
    }
}
