package com.milktea.user.mapper;

import com.milktea.model.entity.WeekSpecial;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WeekSpecialMapper {

    @Select("SELECT * FROM week_special ORDER BY product_id")
    List<WeekSpecial> selectAll();

    @Select("SELECT * FROM week_special WHERE status = 1 ORDER BY product_id")
    List<WeekSpecial> selectActive();

    /** 同商品重复设置时覆盖价格并启用 */
    @Insert("INSERT INTO week_special(product_id, special_price, status) " +
            "VALUES(#{productId}, #{specialPrice}, 1) " +
            "ON DUPLICATE KEY UPDATE special_price = #{specialPrice}, status = 1")
    void upsert(WeekSpecial weekSpecial);

    @Delete("DELETE FROM week_special WHERE product_id = #{productId}")
    void deleteByProductId(@Param("productId") Long productId);
}
