package com.milktea.user.mapper;

import com.milktea.model.entity.Menu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MenuMapper {

    // 注意：menu表字段名与Menu实体不一致，用AS别名映射
    @Select("SELECT id, parent_id, menu_name AS name, path, component, icon, " +
            "menu_type AS type, perms AS permission, sort, status AS visible, create_time " +
            "FROM menu WHERE status = 1 ORDER BY sort")
    List<Menu> findAll();

    @Select("<script>" +
            "SELECT DISTINCT m.perms FROM menu m " +
            "INNER JOIN role_menu rm ON m.id = rm.menu_id " +
            "WHERE m.status = 1 AND m.perms IS NOT NULL AND m.perms != '' " +
            "AND rm.role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<String> findPermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);
}