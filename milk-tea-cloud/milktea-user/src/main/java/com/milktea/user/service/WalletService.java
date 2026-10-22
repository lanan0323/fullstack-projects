package com.milktea.user.service;

import com.milktea.model.entity.WalletFlow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletService {
    Map<String, Object> myWallet();
    BigDecimal recharge(BigDecimal amount);
    List<WalletFlow> flows(int limit);
    // 供订单服务扣款(下单支付/取消退款), C组员后续对接
    boolean deduct(Long userId, BigDecimal amount, String orderNo, boolean refund);
}
