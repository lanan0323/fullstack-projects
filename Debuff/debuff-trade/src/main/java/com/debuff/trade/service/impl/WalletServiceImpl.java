// 包声明
package com.debuff.trade.service.impl;

// 导入所需类
import java.math.BigDecimal;
import java.util.List;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.debuff.trade.mapper.WalletMapper;
import com.debuff.trade.mapper.WalletLogMapper;
import com.debuff.trade.domain.Wallet;
import com.debuff.trade.domain.WalletLog;
import com.debuff.trade.service.IWalletService;

/**
 * 用户钱包Service业务层处理
 * 
 * @author debuff
 * @date 2026-08-25
 */
@Service // 注册为Spring服务Bean
public class WalletServiceImpl implements IWalletService // 钱包服务实现类
{
    /** 流水类型：买入支出 */
    private static final int LOG_TYPE_BUY = 3;

    /** 流水类型：卖出收入 */
    private static final int LOG_TYPE_INCOME = 4;

    /** 流水类型：退款 */
    private static final int LOG_TYPE_REFUND = 5;

    /** 流水类型：充值 */
    private static final int LOG_TYPE_RECHARGE = 1;

    /** 流水类型：提现 */
    private static final int LOG_TYPE_WITHDRAW = 2;

    @Autowired // 自动注入钱包Mapper
    private WalletMapper walletMapper;

    @Autowired // 自动注入流水Mapper
    private WalletLogMapper walletLogMapper;

    /**
     * 查询用户钱包
     * 
     * @param id 用户钱包主键
     * @return 用户钱包
     */
    @Override
    public Wallet selectWalletById(Long id) // 根据ID查询钱包
    {
        return walletMapper.selectWalletById(id); // 委托Mapper查询
    }

    /**
     * 查询用户钱包列表
     * 
     * @param wallet 用户钱包
     * @return 用户钱包
     */
    @Override
    public List<Wallet> selectWalletList(Wallet wallet) // 查询钱包列表
    {
        return walletMapper.selectWalletList(wallet); // 委托Mapper查询列表
    }

    /**
     * 新增用户钱包
     * 
     * @param wallet 用户钱包
     * @return 结果
     */
    @Override
    public int insertWallet(Wallet wallet) // 新增钱包
    {
        wallet.setCreateTime(DateUtils.getNowDate()); // 设置创建时间
        return walletMapper.insertWallet(wallet); // 插入数据库
    }

    /**
     * 修改用户钱包
     * 
     * @param wallet 用户钱包
     * @return 结果
     */
    @Override
    public int updateWallet(Wallet wallet) // 修改钱包
    {
        wallet.setUpdateTime(DateUtils.getNowDate()); // 设置更新时间
        return walletMapper.updateWallet(wallet); // 更新数据库
    }

    /**
     * 批量删除用户钱包
     * 
     * @param ids 需要删除的用户钱包主键
     * @return 结果
     */
    @Override
    public int deleteWalletByIds(Long[] ids) // 批量删除钱包
    {
        return walletMapper.deleteWalletByIds(ids); // 委托Mapper批量删除
    }

    /**
     * 删除用户钱包信息
     * 
     * @param id 用户钱包主键
     * @return 结果
     */
    @Override
    public int deleteWalletById(Long id) // 删除单个钱包
    {
        return walletMapper.deleteWalletById(id); // 委托Mapper删除
    }

    @Override
    public void freezePay(Long userId, BigDecimal amount, String orderNo) // 下单冻结扣款
    {
        Wallet wallet = getWallet(userId); // 查询用户钱包（不存在则抛异常）
        int rows = walletMapper.freezePay(userId, amount); // 执行冻结：余额减、冻结加
        if (rows == 0) // 冻结失败（余额不足）
        {
            throw new ServiceException("余额不足"); // 抛出业务异常
        }
        // 记录买入支出流水，金额为负数表示支出
        saveLog(wallet, LOG_TYPE_BUY, amount.negate(), wallet.getBalance().subtract(amount), orderNo, "购买饰品冻结扣款");
    }

    @Override
    public void unfreezePay(Long userId, BigDecimal amount, String orderNo) // 取消订单退款解冻
    {
        Wallet wallet = getWallet(userId); // 查询用户钱包
        int rows = walletMapper.unfreeze(userId, amount); // 执行解冻：冻结减、余额加
        if (rows == 0) // 解冻失败
        {
            throw new ServiceException("退款失败：冻结金额异常"); // 抛出异常
        }
        // 记录退款流水，金额为正数表示退回余额
        saveLog(wallet, LOG_TYPE_REFUND, amount, wallet.getBalance().add(amount), orderNo, "订单取消退款");
    }

    @Override
    public void settleBuyerFrozen(Long userId, BigDecimal amount, String orderNo) // 确认收货解除买家冻结
    {
        Wallet wallet = getWallet(userId); // 查询用户钱包
        int rows = walletMapper.settleBuyer(userId, amount); // 执行结算：冻结减
        if (rows == 0) // 结算失败
        {
            throw new ServiceException("确认收货失败：冻结金额异常"); // 抛出异常
        }
        // 买入支出流水已在下单时记录，此处不再记流水
    }

    @Override
    public void settleIncome(Long userId, BigDecimal amount, String orderNo) // 卖家收入入账
    {
        Wallet wallet = getWallet(userId); // 查询卖家钱包
        int rows = walletMapper.settleIncome(userId, amount); // 执行入账：余额加、累计收入加
        if (rows == 0) // 入账失败
        {
            throw new ServiceException("卖家入账失败"); // 抛出异常
        }
        // 记录卖出收入流水
        saveLog(wallet, LOG_TYPE_INCOME, amount, wallet.getBalance().add(amount), orderNo, "订单卖出收入");
    }

    @Override
    public Wallet getOrCreateWallet(Long userId) // 获取或自动创建钱包（懒创建）
    {
        Wallet wallet = walletMapper.selectWalletByUserId(userId); // 尝试查询已有钱包
        if (wallet == null) // 钱包不存在
        {
            wallet = new Wallet(); // 创建新钱包对象
            wallet.setUserId(userId); // 设置用户ID
            wallet.setBalance(BigDecimal.ZERO); // 初始余额为0
            wallet.setFrozenAmount(BigDecimal.ZERO); // 初始冻结金额为0
            wallet.setTotalRecharge(BigDecimal.ZERO); // 初始累计充值为0
            wallet.setTotalWithdraw(BigDecimal.ZERO); // 初始累计提现为0
            wallet.setTotalIncome(BigDecimal.ZERO); // 初始累计收入为0
            wallet.setTotalExpense(BigDecimal.ZERO); // 初始累计支出为0
            walletMapper.insertWallet(wallet); // 插入数据库
        }
        return wallet; // 返回钱包对象
    }

    @Override
    public void recharge(Long userId, BigDecimal amount) // 充值
    {
        Wallet wallet = getOrCreateWallet(userId); // 获取钱包（不存在则自动创建）
        int rows = walletMapper.recharge(userId, amount); // 执行充值：余额加、累计充值加
        if (rows == 0) // 充值失败
        {
            throw new ServiceException("充值失败"); // 抛出异常
        }
        // 记录充值流水
        saveLog(wallet, LOG_TYPE_RECHARGE, amount, wallet.getBalance().add(amount), "", "模拟支付充值");
    }

    @Override
    public void withdraw(Long userId, BigDecimal amount) // 提现
    {
        Wallet wallet = getWallet(userId); // 查询钱包（不存在则抛异常）
        int rows = walletMapper.withdraw(userId, amount); // 执行提现：余额减、累计提现加
        if (rows == 0) // 提现失败（余额不足）
        {
            throw new ServiceException("余额不足，无法提现"); // 抛出异常
        }
        // 记录提现流水，金额为负数
        saveLog(wallet, LOG_TYPE_WITHDRAW, amount.negate(), wallet.getBalance().subtract(amount), "", "提现");
    }

    @Override
    public List<WalletLog> selectMyLogs(Long userId) // 查询我的流水
    {
        return walletLogMapper.selectMyWalletLogs(userId); // 委托Mapper查询
    }

    /**
     * 查询钱包，不存在则抛出异常
     */
    private Wallet getWallet(Long userId) // 获取钱包（私有方法）
    {
        Wallet wallet = walletMapper.selectWalletByUserId(userId); // 根据用户ID查询
        if (wallet == null) // 钱包不存在
        {
            throw new ServiceException("用户钱包不存在"); // 抛出异常
        }
        return wallet; // 返回钱包
    }

    /**
     * 记录钱包流水
     */
    private void saveLog(Wallet wallet, Integer type, BigDecimal amount, // 保存流水记录
            BigDecimal balanceAfter, String orderNo, String remark)
    {
        WalletLog log = new WalletLog(); // 创建流水对象
        log.setUserId(wallet.getUserId()); // 设置用户ID
        log.setWalletId(wallet.getId()); // 设置钱包ID
        log.setType(type); // 设置流水类型
        log.setAmount(amount); // 设置金额
        log.setBalanceBefore(wallet.getBalance()); // 设置变动前余额
        log.setBalanceAfter(balanceAfter); // 设置变动后余额
        log.setRelatedOrderNo(orderNo); // 设置关联订单号
        log.setRemark(remark); // 设置备注
        walletLogMapper.insertWalletLog(log); // 插入流水记录
    }
}
