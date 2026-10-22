package com.milktea.product.mapper;

import com.milktea.model.entity.Product;
import com.milktea.model.vo.ProductVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("<script>" +
            "SELECT p.*, c.category_name FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id" +
            "<where>" +
            "<if test='categoryId != null'> AND p.category_id = #{categoryId}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND p.product_name LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "<if test='status != null'> AND p.status = #{status}</if>" +
            "</where>" +
            " ORDER BY p.sort ASC, p.id DESC" +
            "</script>")
    List<ProductVO> page(@Param("categoryId") Long categoryId,
                         @Param("keyword") String keyword,
                         @Param("status") Integer status);

    @Select("SELECT p.*, c.category_name FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id WHERE p.id = #{id}")
    ProductVO findById(Long id);

    @Select("SELECT p.*, c.category_name FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.id " +
            "WHERE p.status = 1 ORDER BY p.sort ASC, p.id ASC")
    List<ProductVO> findOnSale();

    @Insert("INSERT INTO product(category_id, product_name, price, description, image_url, status, sort) " +
            "VALUES(#{categoryId}, #{productName}, #{price}, #{description}, #{imageUrl}, #{status}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);

    @Update("UPDATE product SET category_id = #{categoryId}, product_name = #{productName}, " +
            "price = #{price}, description = #{description}, image_url = #{imageUrl}, " +
            "status = #{status}, sort = #{sort} WHERE id = #{id}")
    void update(Product product);

    @Delete("DELETE FROM product WHERE id = #{id}")
    void deleteById(Long id);

    @Update("UPDATE product SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM product WHERE category_id = #{categoryId}")
    int countByCategoryId(Long categoryId);

    @Insert("INSERT INTO product_spec(product_id, spec_option_id) VALUES(#{productId}, #{specOptionId})")
    void insertProductSpec(@Param("productId") Long productId, @Param("specOptionId") Long specOptionId);

    @Delete("DELETE FROM product_spec WHERE product_id = #{productId}")
    void deleteProductSpecByProductId(Long productId);

    @Select("SELECT st.id AS specTypeId, st.spec_name AS specTypeName, " +
            "so.id AS specOptionId, so.option_name AS specOptionName, so.extra_price " +
            "FROM product_spec ps " +
            "JOIN spec_option so ON ps.spec_option_id = so.id " +
            "JOIN spec_type st ON so.spec_type_id = st.id " +
            "WHERE ps.product_id = #{productId} " +
            "ORDER BY st.sort ASC, so.sort ASC")
    List<ProductVO.SpecOptionVO> findSpecsByProductId(Long productId);
}
