package com.milktea.product.mapper;

import com.milktea.model.entity.SpecType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpecTypeMapper {

    @Select("SELECT * FROM spec_type ORDER BY sort ASC, id ASC")
    List<SpecType> findAll();

    @Select("SELECT * FROM spec_type WHERE id = #{id}")
    SpecType findById(Long id);

    @Insert("INSERT INTO spec_type(spec_name, spec_code, sort, status) " +
            "VALUES(#{specName}, #{specCode}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SpecType specType);

    @Update("UPDATE spec_type SET spec_name = #{specName}, sort = #{sort}, status = #{status} WHERE id = #{id}")
    void update(SpecType specType);

    @Delete("DELETE FROM spec_type WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT COUNT(*) FROM spec_option WHERE spec_type_id = #{id}")
    int countOptionsByTypeId(Long id);
}
