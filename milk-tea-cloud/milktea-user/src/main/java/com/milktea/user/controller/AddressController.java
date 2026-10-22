package com.milktea.user.controller;

import com.milktea.common.context.UserContext;
import com.milktea.common.result.Result;
import com.milktea.model.entity.Address;
import com.milktea.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 外卖收货地址: 挂 /user/address 下, 复用网关 /api/user/** 路由与登录鉴权
 */
@RestController
@RequestMapping("/user/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /** 我的地址列表(默认排最前) */
    @GetMapping("/list")
    public Result<List<Address>> list() {
        return Result.success(addressService.listByUser(UserContext.getCurrentUserId()));
    }

    /** 新增地址 */
    @PostMapping
    public Result<Void> add(@RequestBody Address address) {
        addressService.add(UserContext.getCurrentUserId(), address);
        return Result.success();
    }

    /** 修改地址 */
    @PutMapping
    public Result<Void> update(@RequestBody Address address) {
        addressService.update(UserContext.getCurrentUserId(), address);
        return Result.success();
    }

    /** 删除地址 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(UserContext.getCurrentUserId(), id);
        return Result.success();
    }

    /** 设为默认地址 */
    @PutMapping("/default/{id}")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(UserContext.getCurrentUserId(), id);
        return Result.success();
    }
}
