package com.milktea.store.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 商品配方 Mapper
 */
@Mapper
public interface RecipeMapper {

    /** 某商品的配方行(含原料名/单位/当前库存) */
    @Select("SELECT r.material_id AS materialId, m.name, m.unit, r.quantity, m.current_stock AS currentStock " +
            "FROM product_recipe r JOIN material m ON m.id = r.material_id " +
            "WHERE r.product_id = #{productId} ORDER BY r.id")
    List<Map<String, Object>> findByProductId(@Param("productId") Long productId);

    @Insert("INSERT INTO product_recipe(product_id, material_id, quantity) VALUES(#{productId}, #{materialId}, #{quantity})")
    int insert(@Param("productId") Long productId, @Param("materialId") Long materialId, @Param("quantity") java.math.BigDecimal quantity);

    @Delete("DELETE FROM product_recipe WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
}
