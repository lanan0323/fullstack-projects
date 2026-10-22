package com.milktea.user.mapper;

import com.milktea.model.entity.Wallet;
import com.milktea.model.entity.WalletFlow;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

public interface WalletMapper {

    @Select("SELECT * FROM mt_wallet WHERE user_id = #{userId}")
    Wallet findByUserId(Long userId);

    @Insert("INSERT INTO mt_wallet (user_id, balance) VALUES (#{userId}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Wallet wallet);

    // 加余额(原子自增, 充值/退款用)
    @Update("UPDATE mt_wallet SET balance = balance + #{delta} WHERE user_id = #{userId}")
    int addBalance(@Param("userId") Long userId, @Param("delta") BigDecimal delta);

    // 扣余额: 带余额充足条件, 未扣到(影响行数0)即余额不足
    @Update("UPDATE mt_wallet SET balance = balance - #{amount} WHERE user_id = #{userId} AND balance >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Insert("INSERT INTO mt_wallet_flow (user_id, type, amount, balance_after, order_no) " +
            "VALUES (#{userId}, #{type}, #{amount}, #{balanceAfter}, #{orderNo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFlow(WalletFlow flow);

    @Select("SELECT * FROM mt_wallet_flow WHERE user_id = #{userId} ORDER BY id DESC LIMIT #{limit}")
    List<WalletFlow> findFlows(@Param("userId") Long userId, @Param("limit") int limit);
}
