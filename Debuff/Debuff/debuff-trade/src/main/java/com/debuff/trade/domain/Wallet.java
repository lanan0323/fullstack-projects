// 包声明
package com.debuff.trade.domain;

// 导入所需类
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.debuff.common.annotation.Excel;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 用户钱包对象 debuff_user_wallet
 * 
 * @author debuff
 * @date 2026-08-25
 */
public class Wallet extends BaseEntity // 钱包实体类，继承BaseEntity
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 自增主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 余额 */
    @Excel(name = "余额")
    private BigDecimal balance;

    /** 冻结金额 */
    @Excel(name = "冻结金额")
    private BigDecimal frozenAmount;

    /** 累计充值 */
    @Excel(name = "累计充值")
    private BigDecimal totalRecharge;

    /** 累计提现 */
    @Excel(name = "累计提现")
    private BigDecimal totalWithdraw;

    /** 累计收入 */
    @Excel(name = "累计收入")
    private BigDecimal totalIncome;

    /** 累计支出 */
    @Excel(name = "累计支出")
    private BigDecimal totalExpense;

    /** 用户名（联查） */
    private String userName;

    /** 昵称（联查） */
    private String nickName;

    /** 设置主键ID */
    public void setId(Long id) 
    {
        this.id = id;
    }

    /** 获取主键ID */
    public Long getId() 
    {
        return id;
    }

    /** 设置用户ID */
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    /** 获取用户ID */
    public Long getUserId() 
    {
        return userId;
    }

    /** 设置余额 */
    public void setBalance(BigDecimal balance) 
    {
        this.balance = balance;
    }

    /** 获取余额 */
    public BigDecimal getBalance() 
    {
        return balance;
    }

    /** 设置冻结金额 */
    public void setFrozenAmount(BigDecimal frozenAmount) 
    {
        this.frozenAmount = frozenAmount;
    }

    /** 获取冻结金额 */
    public BigDecimal getFrozenAmount() 
    {
        return frozenAmount;
    }

    /** 设置累计充值 */
    public void setTotalRecharge(BigDecimal totalRecharge) 
    {
        this.totalRecharge = totalRecharge;
    }

    /** 获取累计充值 */
    public BigDecimal getTotalRecharge() 
    {
        return totalRecharge;
    }

    /** 设置累计提现 */
    public void setTotalWithdraw(BigDecimal totalWithdraw) 
    {
        this.totalWithdraw = totalWithdraw;
    }

    /** 获取累计提现 */
    public BigDecimal getTotalWithdraw() 
    {
        return totalWithdraw;
    }

    /** 设置累计收入 */
    public void setTotalIncome(BigDecimal totalIncome) 
    {
        this.totalIncome = totalIncome;
    }

    /** 获取累计收入 */
    public BigDecimal getTotalIncome() 
    {
        return totalIncome;
    }

    /** 设置累计支出 */
    public void setTotalExpense(BigDecimal totalExpense) 
    {
        this.totalExpense = totalExpense;
    }

    /** 获取累计支出 */
    public BigDecimal getTotalExpense() 
    {
        return totalExpense;
    }

    /** 获取用户名 */
    public String getUserName()
    {
        return userName;
    }

    /** 设置用户名 */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /** 获取昵称 */
    public String getNickName()
    {
        return nickName;
    }

    /** 设置昵称 */
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    @Override
    public String toString() { // 转字符串方法
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId()) // 主键ID
            .append("userId", getUserId()) // 用户ID
            .append("balance", getBalance()) // 余额
            .append("frozenAmount", getFrozenAmount()) // 冻结金额
            .append("totalRecharge", getTotalRecharge()) // 累计充值
            .append("totalWithdraw", getTotalWithdraw()) // 累计提现
            .append("totalIncome", getTotalIncome()) // 累计收入
            .append("totalExpense", getTotalExpense()) // 累计支出
            .append("createTime", getCreateTime()) // 创建时间
            .append("updateTime", getUpdateTime()) // 更新时间
            .toString();
    }
}
