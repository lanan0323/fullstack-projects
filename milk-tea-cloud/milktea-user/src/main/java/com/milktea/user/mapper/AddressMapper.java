package com.milktea.user.mapper;

import com.milktea.model.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    @Select("SELECT * FROM mt_address WHERE user_id = #{userId} ORDER BY is_default DESC, update_time DESC")
    List<Address> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM mt_address WHERE id = #{id}")
    Address selectById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM mt_address WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO mt_address(user_id, receiver, phone, address, is_default) " +
            "VALUES(#{userId}, #{receiver}, #{phone}, #{address}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Address address);

    @Update("UPDATE mt_address SET receiver = #{receiver}, phone = #{phone}, " +
            "address = #{address}, is_default = #{isDefault} WHERE id = #{id}")
    void update(Address address);

    /** 该用户全部地址取消默认 */
    @Update("UPDATE mt_address SET is_default = 0 WHERE user_id = #{userId}")
    void clearDefault(@Param("userId") Long userId);

    @Update("UPDATE mt_address SET is_default = 1 WHERE id = #{id}")
    void setDefault(@Param("id") Long id);

    @Delete("DELETE FROM mt_address WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
