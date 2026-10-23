// 包声明
package com.debuff.trade.mapper;

// 导入所需类
import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.debuff.trade.domain.Wallet;

/**
 * 用户钱包Mapper接口
 * 
 * @author debuff
 * @date 2026-08-25
 */
public interface WalletMapper // 用户钱包Mapper接口，定义钱包相关数据库操作
{
    /**
     * 查询用户钱包
     * 
     * @param id 用户钱包主键
     * @return 用户钱包
     */
    public Wallet selectWalletById(Long id);

    /**
     * 查询用户钱包列表
     * 
     * @param wallet 用户钱包
     * @return 用户钱包集合
     */
    public List<Wallet> selectWalletList(Wallet wallet);

    /**
     * 新增用户钱包
     * 
     * @param wallet 用户钱包
     * @return 结果
     */
    public int insertWallet(Wallet wallet);

    /**
     * 修改用户钱包
     * 
     * @param wallet 用户钱包
     * @return 结果
     */
    public int updateWallet(Wallet wallet);

    /**
     * 删除用户钱包
     * 
     * @param id 用户钱包主键
     * @return 结果
     */
    public int deleteWalletById(Long id);

    /**
     * 批量删除用户钱包
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWalletByIds(Long[] ids);

    /**
     * 通过用户ID查询钱包
     * 
     * @param userId 用户ID
     * @return 钱包
     */
    public Wallet selectWalletByUserId(Long userId);

    /**
     * 下单冻结扣款：余额减少、冻结增加、累计支出增加
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @return 受影响行数（0表示余额不足）
     */
    public int freezePay(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 取消退款解冻：冻结减少、余额增加
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @return 受影响行数（0表示冻结金额不足）
     */
    public int unfreeze(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 确认收货：买家冻结金额减少（钱已支出，仅解除冻结占用）
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @return 受影响行数
     */
    public int settleBuyer(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 卖家入账：余额增加、累计收入增加
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @return 受影响行数
     */
    public int settleIncome(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 充值：余额增加、累计充值增加
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @return 受影响行数
     */
    public int recharge(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 提现：余额减少、累计提现增加
     * 
     * @param userId 用户ID
     * @param amount 金额
     * @return 受影响行数（0表示余额不足）
     */
    public int withdraw(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
