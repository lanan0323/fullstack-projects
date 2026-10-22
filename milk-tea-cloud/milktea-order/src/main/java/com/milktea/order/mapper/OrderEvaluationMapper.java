package com.milktea.order.mapper;

import com.milktea.model.entity.OrderEvaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderEvaluationMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO order_evaluation(order_id, user_id, rating, content, create_time) " +
            "VALUES(#{orderId}, #{userId}, #{rating}, #{content}, #{createTime})")
    void insert(OrderEvaluation evaluation);

    @Select("SELECT id, order_id, user_id, rating, content, create_time " +
            "FROM order_evaluation WHERE order_id = #{orderId}")
    OrderEvaluation selectByOrderId(@Param("orderId") Long orderId);
}
