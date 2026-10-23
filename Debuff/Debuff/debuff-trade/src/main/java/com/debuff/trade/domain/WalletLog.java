// 包声明
package com.debuff.trade.domain;

// 导入所需类
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.debuff.common.annotation.Excel;
import com.debuff.common.core.domain.BaseEntity;

/**
 * 钱包流水对象 debuff_wallet_log
 * 
 * @author debuff
 * @date 2026-08-25
 */
public class WalletLog extends BaseEntity // 钱包流水实体类
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 自增主键 */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 钱包ID */
    @Excel(name = "钱包ID")
    private Long walletId;

    /** 类型 */
    @Excel(name = "类型")
    private Integer type;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 变动前余额 */
    @Excel(name = "变动前余额")
    private BigDecimal balanceBefore;

    /** 变动后余额 */
    @Excel(name = "变动后余额")
    private BigDecimal balanceAfter;

    /** 关联订单编号 */
    @Excel(name = "关联订单编号")
    private String relatedOrderNo;

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

    /** 设置钱包ID */
    public void setWalletId(Long walletId) 
    {
        this.walletId = walletId;
    }

    /** 获取钱包ID */
    public Long getWalletId() 
    {
        return walletId;
    }

    /** 设置类型 */
    public void setType(Integer type) 
    {
        this.type = type;
    }

    /** 获取类型 */
    public Integer getType() 
    {
        return type;
    }

    /** 设置金额 */
    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    /** 获取金额 */
    public BigDecimal getAmount() 
    {
        return amount;
    }

    /** 设置变动前余额 */
    public void setBalanceBefore(BigDecimal balanceBefore) 
    {
        this.balanceBefore = balanceBefore;
    }

    /** 获取变动前余额 */
    public BigDecimal getBalanceBefore() 
    {
        return balanceBefore;
    }

    /** 设置变动后余额 */
    public void setBalanceAfter(BigDecimal balanceAfter) 
    {
        this.balanceAfter = balanceAfter;
    }

    /** 获取变动后余额 */
    public BigDecimal getBalanceAfter() 
    {
        return balanceAfter;
    }

    /** 设置关联订单编号 */
    public void setRelatedOrderNo(String relatedOrderNo) 
    {
        this.relatedOrderNo = relatedOrderNo;
    }

    /** 获取关联订单编号 */
    public String getRelatedOrderNo() 
    {
        return relatedOrderNo;
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
            .append("walletId", getWalletId()) // 钱包ID
            .append("type", getType()) // 类型
            .append("amount", getAmount()) // 金额
            .append("balanceBefore", getBalanceBefore()) // 变动前余额
            .append("balanceAfter", getBalanceAfter()) // 变动后余额
            .append("relatedOrderNo", getRelatedOrderNo()) // 关联订单号
            .append("remark", getRemark()) // 备注
            .append("createTime", getCreateTime()) // 创建时间
            .toString();
    }
}
