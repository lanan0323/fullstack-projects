// 包声明
package com.debuff.trade.service;

// 导入所需类
import java.math.BigDecimal;
import java.util.List;
import com.debuff.trade.domain.Wallet;
import com.debuff.trade.domain.WalletLog;

/**
 * 用户钱包Service接口
 * 
 * @author debuff
 * @date 2026-08-25
 */
public interface IWalletService // 钱包服务接口
{
    /**
     * 查询用户钱包
     * 
     * @param id 用户钱包主键
     * @return 用户钱包
     */
    public Wallet selectWalletById(Long id); // 根据ID查询钱包

    /**
     * 查询用户钱包列表
     * 
     * @param wallet 用户钱包
     * @return 用户钱包集合
     */
    public List<Wallet> selectWalletList(Wallet wallet); // 查询钱包列表

    /**
     * 新增用户钱包
     * 
     * @param wallet 用户钱包
     * @return 结果
     */
    public int insertWallet(Wallet wallet); // 新增钱包

    /**
     * 修改用户钱包
     * 
     * @param wallet 用户钱包
     * @return 结果
     */
    public int updateWallet(Wallet wallet); // 修改钱包

    /**
     * 批量删除用户钱包
     * 
     * @param ids 需要删除的用户钱包主键集合
     * @return 结果
     */
    public int deleteWalletByIds(Long[] ids); // 批量删除钱包

    /**
     * 删除用户钱包信息
     * 
     * @param id 用户钱包主键
     * @return 结果
     */
    public int deleteWalletById(Long id); // 删除单个钱包

    /**
     * 下单冻结扣款（余额减、冻结加、记买入支出流水）
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @param orderNo 关联订单号
     */
    public void freezePay(Long userId, BigDecimal amount, String orderNo); // 冻结余额用于支付

    /**
     * 取消订单退款（冻结减、余额加、记退款流水）
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @param orderNo 关联订单号
     */
    public void unfreezePay(Long userId, BigDecimal amount, String orderNo); // 解冻退款

    /**
     * 确认收货解除买家冻结（仅冻结减，买入支出已在下单时记录）
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @param orderNo 关联订单号
     */
    public void settleBuyerFrozen(Long userId, BigDecimal amount, String orderNo); // 结算买家冻结

    /**
     * 卖家入账（余额加、累计收入加、记卖出收入流水）
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @param orderNo 关联订单号
     */
    public void settleIncome(Long userId, BigDecimal amount, String orderNo); // 卖家收入入账

    /**
     * 查询用户钱包，不存在则自动创建（用户端懒创建）
     * 
     * @param userId 用户ID
     * @return 钱包
     */
    public Wallet getOrCreateWallet(Long userId); // 获取或自动创建钱包

    /**
     * 充值（余额加、累计充值加、记充值流水）
     * 
     * @param userId 用户ID
     * @param amount 金额
     */
    public void recharge(Long userId, BigDecimal amount); // 充值

    /**
     * 提现（余额减、累计提现加、记提现流水）
     * 
     * @param userId 用户ID
     * @param amount 金额
     */
    public void withdraw(Long userId, BigDecimal amount); // 提现

    /**
     * 查询我的钱包流水（按时间倒序）
     * 
     * @param userId 用户ID
     * @return 流水集合
     */
    public List<WalletLog> selectMyLogs(Long userId); // 查询我的流水
}
