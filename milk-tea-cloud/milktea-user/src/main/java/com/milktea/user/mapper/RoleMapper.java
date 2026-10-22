package com.milktea.user.mapper;

import com.milktea.model.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("SELECT * FROM role WHERE status = 1 ORDER BY id")
    List<Role> findAll();

    @Select("SELECT * FROM role WHERE id = #{id}")
    Role findById(Long id);

    @Insert("INSERT INTO role(role_name, role_code, description, status) " +
            "VALUES(#{roleName}, #{roleCode}, #{description}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);

    @Update("UPDATE role SET role_name = #{roleName}, role_code = #{roleCode}, description = #{description} WHERE id = #{id}")
    int update(Role role);

    @Delete("DELETE FROM role WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT menu_id FROM role_menu WHERE role_id = #{roleId}")
    List<Long> findMenuIdsByRoleId(Long roleId);

    @Insert("<script>" +
            "INSERT INTO role_menu(role_id, menu_id) VALUES " +
            "<foreach collection='menuIds' item='menuId' separator=','>" +
            "(#{roleId}, #{menuId})" +
            "</foreach>" +
            "</script>")
    int insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    @Delete("DELETE FROM role_menu WHERE role_id = #{roleId}")
    int deleteRoleMenus(Long roleId);

    @Insert("INSERT INTO user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    int deleteUserRoles(Long userId);

    @Select("SELECT role_id FROM user_role WHERE user_id = #{userId}")
    List<Long> findRoleIdsByUserId(Long userId);
}