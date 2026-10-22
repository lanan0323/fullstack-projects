package com.milktea.user.service.impl;

import com.milktea.common.context.UserContext;
import com.milktea.common.exception.BusinessException;
import com.milktea.model.entity.Wallet;
import com.milktea.model.entity.WalletFlow;
import com.milktea.user.mapper.WalletMapper;
import com.milktea.user.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletServiceImpl implements WalletService {

    private static final BigDecimal MAX_RECHARGE = new BigDecimal("5000");

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private com.milktea.user.service.PointsService pointsService;

    @Override
    public Map<String, Object> myWallet() {
        Wallet wallet = getOrCreate(UserContext.getCurrentUserId());
        Map<String, Object> result = new HashMap<>();
        result.put("balance", wallet.getBalance());
        return result;
    }

    @Override
    @Transactional
    public BigDecimal recharge(BigDecimal amount) {
        if (amount == null || amount.compareTo(new BigDecimal("0.01")) < 0) throw new BusinessException("充值金额至少0.01元");
        if (amount.stripTrailingZeros().scale() > 2) throw new BusinessException("金额最多两位小数");
        if (amount.compareTo(MAX_RECHARGE) > 0) throw new BusinessException("单笔充值不能超过" + MAX_RECHARGE + "元");
        Long userId = UserContext.getCurrentUserId();
        getOrCreate(userId);
        walletMapper.addBalance(userId, amount);
        BigDecimal after = walletMapper.findByUserId(userId).getBalance();
        recordFlow(userId, 1, amount, after, null);
        // 充值送积分: 1元=1积分
        pointsService.earn(userId, amount.longValue(), "充值赠送");
        return after;
    }

    @Override
    public List<WalletFlow> flows(int limit) {
        if (limit <= 0 || limit > 100) limit = 50;
        return walletMapper.findFlows(UserContext.getCurrentUserId(), limit);
    }

    // 订单服务调用: 扣款(type=2)或退款(type=3), refund=true为退款
    @Override
    @Transactional
    public boolean deduct(Long userId, BigDecimal amount, String orderNo, boolean refund) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessException("金额参数不合法");
        getOrCreate(userId);
        if (refund) {
            walletMapper.addBalance(userId, amount);
            recordFlow(userId, 3, amount, walletMapper.findByUserId(userId).getBalance(), orderNo);
            return true;
        }
        int rows = walletMapper.deductBalance(userId, amount);
        if (rows == 0) throw new BusinessException("余额不足, 请先充值");
        recordFlow(userId, 2, amount, walletMapper.findByUserId(userId).getBalance(), orderNo);
        return true;
    }

    private Wallet getOrCreate(Long userId) {
        Wallet wallet = walletMapper.findByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            walletMapper.insert(wallet);
            wallet.setBalance(BigDecimal.ZERO);
        }
        return wallet;
    }

    private void recordFlow(Long userId, int type, BigDecimal amount, BigDecimal after, String orderNo) {
        WalletFlow flow = new WalletFlow();
        flow.setUserId(userId);
        flow.setType(type);
        flow.setAmount(amount);
        flow.setBalanceAfter(after);
        flow.setOrderNo(orderNo);
        walletMapper.insertFlow(flow);
    }
}
