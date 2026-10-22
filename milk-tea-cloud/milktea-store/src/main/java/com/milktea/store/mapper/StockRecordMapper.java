package com.milktea.store.mapper;

import com.milktea.model.entity.StockRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 库存变动流水 Mapper（注解式 MyBatis）
 * 负责人: 成员D
 */
@Mapper
public interface StockRecordMapper {

    @Select("<script>" +
            "SELECT id, material_id, material_name, type, quantity, before_stock, after_stock, operator, remark, create_time " +
            "FROM stock_record" +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>material_name LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "</where>" +
            "ORDER BY create_time DESC, id DESC" +
            "</script>")
    List<StockRecord> page(@Param("keyword") String keyword);

    @Select("SELECT id, material_id, material_name, type, quantity, before_stock, after_stock, operator, remark, create_time " +
            "FROM stock_record WHERE material_id = #{materialId} ORDER BY create_time DESC, id DESC")
    List<StockRecord> findByMaterialId(@Param("materialId") Long materialId);

    @Insert("INSERT INTO stock_record (material_id, material_name, type, quantity, before_stock, after_stock, operator, remark, create_time) " +
            "VALUES (#{materialId}, #{materialName}, #{type}, #{quantity}, #{beforeStock}, #{afterStock}, #{operator}, #{remark}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StockRecord record);

    @Select("SELECT COUNT(*) FROM stock_record WHERE material_id = #{materialId}")
    long countByMaterialId(@Param("materialId") Long materialId);
}
