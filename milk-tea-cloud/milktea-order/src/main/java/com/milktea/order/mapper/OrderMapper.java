package com.milktea.order.mapper;

import com.milktea.model.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 新增订单, 自增主键回填 id
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO orders(order_no, user_id, total_amount, discount_amount, coupon_id, status, remark, create_time, update_time) " +
            "VALUES(#{orderNo}, #{userId}, #{totalAmount}, #{discountAmount}, #{couponId}, #{status}, #{remark}, #{createTime}, #{updateTime})")
    void insert(Orders order);

    @Select("SELECT id, order_no, user_id, total_amount, discount_amount, coupon_id, status, remark, create_time, update_time, " +
            "finish_time AS complete_time FROM orders WHERE id = #{id}")
    Orders selectById(@Param("id") Long id);

    /**
     * 条件分页: status(精确) / keyword(订单号模糊)
     */
    @Select("<script>SELECT id, order_no, user_id, total_amount, discount_amount, coupon_id, status, remark, create_time, update_time, " +
            "finish_time AS complete_time FROM orders" +
            "<where>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND order_no LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "</where> ORDER BY create_time DESC, id DESC</script>")
    List<Orders> selectPage(@Param("status") Integer status, @Param("keyword") String keyword);

    @Select("SELECT id, order_no, user_id, total_amount, discount_amount, coupon_id, status, remark, create_time, update_time, " +
            "finish_time AS complete_time FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC, id DESC")
    List<Orders> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT id, order_no, user_id, total_amount, discount_amount, coupon_id, status, remark, create_time, update_time, " +
            "finish_time AS complete_time FROM orders WHERE user_id = #{userId} AND status = #{status} " +
            "ORDER BY create_time DESC, id DESC")
    List<Orders> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    // ===== 店铺统计(store服务Feign内部调用) 口径: 营业额只算已完成(status=2), 今日订单不含已取消(status<3) =====

    @Select("SELECT COUNT(*) FROM orders WHERE create_time >= CURRENT_DATE AND status < 3")
    int countToday();

    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM orders WHERE finish_time >= CURRENT_DATE AND status = 2")
    java.math.BigDecimal sumTodayRevenue();

    /** 近N日营业额趋势(按完成日期分组, 只含已完成订单) */
    @Select("SELECT DATE_FORMAT(finish_time, '%m-%d') AS `date`, IFNULL(SUM(total_amount), 0) AS revenue " +
            "FROM orders WHERE status = 2 AND finish_time >= DATE_SUB(CURRENT_DATE, INTERVAL #{days} DAY) " +
            "GROUP BY DATE_FORMAT(finish_time, '%m-%d') ORDER BY MIN(finish_time)")
    List<java.util.Map<String, Object>> revenueTrend(@Param("days") int days);

    /**
     * 更新状态; 完成时一并写入 finish_time
     */
    @Update("<script>UPDATE orders SET status = #{status}, update_time = #{updateTime}" +
            "<if test='finishTime != null'>, finish_time = #{finishTime}</if>" +
            " WHERE id = #{id}</script>")
    void updateStatus(@Param("id") Long id,
                      @Param("status") Integer status,
                      @Param("updateTime") LocalDateTime updateTime,
                      @Param("finishTime") LocalDateTime finishTime);
}
