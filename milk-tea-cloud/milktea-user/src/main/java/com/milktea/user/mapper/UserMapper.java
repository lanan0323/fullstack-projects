package com.milktea.user.mapper;

import com.milktea.model.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE phone = #{phone} AND status = 1")
    User findByPhone(String phone);

    @Select("SELECT * FROM user WHERE username = #{username} AND status = 1")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Insert("INSERT INTO user(username, phone, password, nickname, avatar, user_type, status) " +
            "VALUES(#{username}, #{phone}, #{password}, #{nickname}, #{avatar}, #{userType}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE user SET nickname = #{nickname}, avatar = #{avatar} WHERE id = #{id}")
    int updateInfo(@Param("id") Long id, @Param("nickname") String nickname, @Param("avatar") String avatar);

    @Update("UPDATE user SET avatar = #{avatar} WHERE id = #{id}")
    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone}")
    int countByPhone(String phone);
}