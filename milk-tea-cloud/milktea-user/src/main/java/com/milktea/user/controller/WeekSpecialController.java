package com.milktea.user.controller;

import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.Result;
import com.milktea.model.entity.WeekSpecial;
import com.milktea.user.mapper.WeekSpecialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 每周9.9特价活动配置: 挂 /user/weekspecial 下, 复用网关 /api/user/** 路由与鉴权
 * 管理端: list/set/remove 配置活动; 用户端mall: active 取生效活动
 */
@RestController
@RequestMapping("/user/weekspecial")
public class WeekSpecialController {

    @Autowired
    private WeekSpecialMapper weekSpecialMapper;

    /** 活动配置全量(管理端) */
    @GetMapping("/list")
    public Result<List<WeekSpecial>> list() {
        return Result.success(weekSpecialMapper.selectAll());
    }

    /** 生效中的活动(用户端mall) */
    @GetMapping("/active")
    public Result<List<WeekSpecial>> active() {
        return Result.success(weekSpecialMapper.selectActive());
    }

    /** 设置/更新活动价(同商品覆盖并启用) */
    @PostMapping
    public Result<Void> set(@RequestBody WeekSpecial weekSpecial) {
        if (weekSpecial.getProductId() == null) {
            throw new BusinessException("缺少商品ID");
        }
        BigDecimal price = weekSpecial.getSpecialPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("活动价必须大于0");
        }
        weekSpecialMapper.upsert(weekSpecial);
        return Result.success();
    }

    /** 取消活动(删除配置) */
    @DeleteMapping("/{productId}")
    public Result<Void> remove(@PathVariable Long productId) {
        weekSpecialMapper.deleteByProductId(productId);
        return Result.success();
    }
}
