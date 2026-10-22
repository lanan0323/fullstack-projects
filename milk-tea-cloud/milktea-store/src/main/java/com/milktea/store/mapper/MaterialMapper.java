package com.milktea.store.mapper;

import com.milktea.model.entity.Material;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 原料 Mapper（注解式 MyBatis）
 * 负责人: 成员D
 */
@Mapper
public interface MaterialMapper {

    @Select("SELECT id, name, unit, current_stock, min_threshold, status, create_time, update_time " +
            "FROM material WHERE id = #{id}")
    Material findById(@Param("id") Long id);

    @Select("<script>" +
            "SELECT id, name, unit, current_stock, min_threshold, status, create_time, update_time FROM material" +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>name LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "</where>" +
            "ORDER BY id" +
            "</script>")
    List<Material> page(@Param("keyword") String keyword);

    @Select("SELECT id, name, unit, current_stock, min_threshold, status, create_time, update_time " +
            "FROM material ORDER BY id")
    List<Material> list();

    @Insert("INSERT INTO material (name, unit, current_stock, min_threshold, status, create_time, update_time) " +
            "VALUES (#{name}, #{unit}, #{currentStock}, #{minThreshold}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Material material);

    @Update("UPDATE material SET name = #{name}, unit = #{unit}, min_threshold = #{minThreshold}, " +
            "status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    int update(Material material);

    @Delete("DELETE FROM material WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE material SET current_stock = #{stock}, update_time = #{updateTime} WHERE id = #{id}")
    int updateStock(@Param("id") Long id, @Param("stock") BigDecimal stock, @Param("updateTime") LocalDateTime updateTime);

    @Select("SELECT COUNT(*) FROM material WHERE current_stock < min_threshold")
    long countWarning();

    @Select("SELECT COUNT(*) FROM material")
    long countTotal();

    /** 原子扣减: 库存不足时影响行数为0, 防并发超扣 */
    @Update("UPDATE material SET current_stock = current_stock - #{qty}, update_time = #{updateTime} " +
            "WHERE id = #{id} AND current_stock >= #{qty}")
    int deductStock(@Param("id") Long id, @Param("qty") BigDecimal qty, @Param("updateTime") LocalDateTime updateTime);
}
