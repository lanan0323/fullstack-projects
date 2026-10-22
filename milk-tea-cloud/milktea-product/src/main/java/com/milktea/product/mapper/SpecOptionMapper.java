package com.milktea.product.mapper;

import com.milktea.model.entity.SpecOption;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpecOptionMapper {

    @Select("SELECT * FROM spec_option WHERE spec_type_id = #{typeId} ORDER BY sort ASC, id ASC")
    List<SpecOption> findByTypeId(Long typeId);

    @Select("SELECT * FROM spec_option WHERE id = #{id}")
    SpecOption findById(Long id);

    @Insert("INSERT INTO spec_option(spec_type_id, option_name, extra_price, sort, status) " +
            "VALUES(#{specTypeId}, #{optionName}, #{extraPrice}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SpecOption specOption);

    @Update("UPDATE spec_option SET option_name = #{optionName}, extra_price = #{extraPrice}, " +
            "sort = #{sort}, status = #{status} WHERE id = #{id}")
    void update(SpecOption specOption);

    @Delete("DELETE FROM spec_option WHERE id = #{id}")
    void deleteById(Long id);
}
