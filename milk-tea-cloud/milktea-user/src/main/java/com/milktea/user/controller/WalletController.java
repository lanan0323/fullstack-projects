package com.milktea.user.controller;

import com.milktea.common.result.Result;
import com.milktea.model.entity.WalletFlow;
import com.milktea.user.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户钱包: 余额/充值/明细(挂/user/wallet, 复用网关/api/user/**路由与登录鉴权)
 */
@RestController
@RequestMapping("/user/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping
    public Result<Map<String, Object>> myWallet() {
        return Result.success(walletService.myWallet());
    }

    // 模拟支付充值
    @PostMapping("/recharge")
    public Result<Map<String, Object>> recharge(@RequestBody Map<String, BigDecimal> body) {
        BigDecimal after = walletService.recharge(body.get("amount"));
        return Result.success(Map.of("balance", after));
    }

    @GetMapping("/flows")
    public Result<List<WalletFlow>> flows(@RequestParam(defaultValue = "50") int limit) {
        return Result.success(walletService.flows(limit));
    }
}
