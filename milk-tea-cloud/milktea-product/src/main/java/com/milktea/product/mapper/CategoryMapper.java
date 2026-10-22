package com.milktea.product.mapper;

import com.milktea.model.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category ORDER BY sort ASC, id ASC")
    List<Category> findAll();

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(Long id);

    @Insert("INSERT INTO category(parent_id, category_name, sort, status) " +
            "VALUES(#{parentId}, #{categoryName}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Update("UPDATE category SET parent_id = #{parentId}, category_name = #{categoryName}, " +
            "sort = #{sort}, status = #{status} WHERE id = #{id}")
    void update(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT COUNT(*) FROM category WHERE parent_id = #{id}")
    int countByParentId(Long id);
}
