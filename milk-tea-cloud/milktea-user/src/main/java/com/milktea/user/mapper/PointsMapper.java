package com.milktea.user.mapper;

import com.milktea.model.entity.PointsAccount;
import com.milktea.model.entity.PointsFlow;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PointsMapper {

    @Select("SELECT * FROM mt_points WHERE user_id = #{userId}")
    PointsAccount findByUserId(Long userId);

    @Insert("INSERT INTO mt_points (user_id, points) VALUES (#{userId}, #{points})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointsAccount account);

    @Update("UPDATE mt_points SET points = points + #{delta} WHERE user_id = #{userId}")
    int addPoints(@Param("userId") Long userId, @Param("delta") Long delta);

    // 条件扣减防超扣: 余额不足时影响行数为0
    @Update("UPDATE mt_points SET points = points - #{delta} WHERE user_id = #{userId} AND points >= #{delta}")
    int deductPoints(@Param("userId") Long userId, @Param("delta") Long delta);

    @Insert("INSERT INTO mt_points_flow (user_id, type, points, remark) VALUES (#{userId}, #{type}, #{points}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertFlow(PointsFlow flow);

    @Select("SELECT * FROM mt_points_flow WHERE user_id = #{userId} ORDER BY id DESC LIMIT #{limit}")
    List<PointsFlow> findFlows(@Param("userId") Long userId, @Param("limit") int limit);

    // 升级礼包发放凭据: 是否已记过某等级礼包流水
    @Select("SELECT COUNT(*) FROM mt_points_flow WHERE user_id = #{userId} AND remark = #{remark}")
    int countFlowByRemark(@Param("userId") Long userId, @Param("remark") String remark);

    // 下单消费积分结算去重
    @Select("SELECT COUNT(*) FROM mt_points_order WHERE order_id = #{orderId}")
    int countSettled(Long orderId);

    @Insert("INSERT INTO mt_points_order (order_id, user_id, points, order_no) VALUES (#{orderId}, #{userId}, #{points}, #{orderNo})")
    int insertSettle(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("points") long points, @Param("orderNo") String orderNo);
}
