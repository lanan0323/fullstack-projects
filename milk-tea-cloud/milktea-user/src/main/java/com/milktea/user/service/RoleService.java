package com.milktea.user.service;

import com.milktea.model.entity.Menu;
import com.milktea.model.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> list();
    Role queryById(Long id);
    void add(Role role);
    void update(Role role);
    void delete(Long id);
    void assignMenus(Long roleId, List<Long> menuIds);
    List<Menu> getMenuTree();
}