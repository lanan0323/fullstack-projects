package com.milktea.user.mapper;

import com.milktea.model.entity.Coupon;
import com.milktea.model.entity.UserCoupon;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CouponMapper {

    @Select("SELECT * FROM mt_coupon WHERE id = #{id}")
    Coupon selectById(@Param("id") Long id);

    /** 可领券列表(排除[等级]前缀的系统礼券, 那些仅随等级礼包自动发放) */
    @Select("SELECT * FROM mt_coupon WHERE status = 1 AND name NOT LIKE '[等级]%' ORDER BY id")
    List<Coupon> selectOnSale();

    /** 按名称查券模板id(等级礼包发放用) */
    @Select("SELECT id FROM mt_coupon WHERE name = #{name} LIMIT 1")
    Long selectIdByName(@Param("name") String name);

    @Select("SELECT COUNT(*) FROM mt_user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId} AND status = 0")
    int countUnused(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /** 扣减库存, 条件remain>0防止超领, 返回影响行数 */
    @Update("UPDATE mt_coupon SET remain = remain - 1 WHERE id = #{id} AND remain > 0")
    int decreaseRemain(@Param("id") Long id);

    @Insert("INSERT INTO mt_user_coupon(user_id, coupon_id, status, receive_time, expire_time) " +
            "VALUES(#{userId}, #{couponId}, 0, NOW(), #{expireTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserCoupon userCoupon);

    @Select("SELECT uc.id, uc.user_id, uc.coupon_id, uc.status, uc.receive_time, uc.expire_time, " +
            "c.name, c.type, c.amount, c.min_amount " +
            "FROM mt_user_coupon uc JOIN mt_coupon c ON uc.coupon_id = c.id " +
            "WHERE uc.user_id = #{userId} AND uc.status = 0 ORDER BY uc.expire_time")
    List<UserCoupon> selectMyUnused(@Param("userId") Long userId);

    /** 查用户某券的一张未使用且未过期的票券(取最快到期的一张) */
    @Select("SELECT * FROM mt_user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId} " +
            "AND status = 0 AND expire_time > NOW() ORDER BY expire_time LIMIT 1")
    UserCoupon selectUnusedById(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /** 核销票券, 条件更新防止并发重复核销 */
    @Update("UPDATE mt_user_coupon SET status = 1 WHERE id = #{id} AND status = 0")
    int markUsed(@Param("id") Long id);

    /** 取消订单回退票券(只回退一张) */
    @Update("UPDATE mt_user_coupon SET status = 0 WHERE user_id = #{userId} AND coupon_id = #{couponId} " +
            "AND status = 1 LIMIT 1")
    int restore(@Param("userId") Long userId, @Param("couponId") Long couponId);

    // ==================== 管理端 ====================

    /** 券模板分页查询 */
    @Select("<script>SELECT * FROM mt_coupon <where>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</where> ORDER BY id DESC</script>")
    List<Coupon> selectPage(@Param("name") String name, @Param("status") Integer status);

    @Insert("INSERT INTO mt_coupon(name, type, amount, min_amount, remain, valid_days, status) " +
            "VALUES(#{name}, #{type}, #{amount}, #{minAmount}, #{remain}, #{validDays}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertCoupon(Coupon coupon);

    @Update("<script>UPDATE mt_coupon <set>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='type != null'>type = #{type},</if>" +
            "<if test='amount != null'>amount = #{amount},</if>" +
            "<if test='minAmount != null'>min_amount = #{minAmount},</if>" +
            "<if test='remain != null'>remain = #{remain},</if>" +
            "<if test='validDays != null'>valid_days = #{validDays},</if>" +
            "</set> WHERE id = #{id}</script>")
    void updateCoupon(Coupon coupon);

    @Update("UPDATE mt_coupon SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM mt_user_coupon WHERE coupon_id = #{couponId}")
    int countRecords(@Param("couponId") Long couponId);

    @Delete("DELETE FROM mt_coupon WHERE id = #{id}")
    void deleteById(Long id);

    /** 领取/发放记录(联查券模板与用户昵称手机号) */
    @Select("<script>SELECT uc.id, uc.user_id, uc.coupon_id, uc.status, uc.receive_time, uc.expire_time, " +
            "c.name, c.type, c.amount, c.min_amount, u.nickname, u.phone " +
            "FROM mt_user_coupon uc JOIN mt_coupon c ON uc.coupon_id = c.id " +
            "JOIN user u ON uc.user_id = u.id <where>" +
            "<if test='couponId != null'>AND uc.coupon_id = #{couponId}</if>" +
            "</where> ORDER BY uc.id DESC</script>")
    List<UserCoupon> selectRecords(@Param("couponId") Long couponId);
}
