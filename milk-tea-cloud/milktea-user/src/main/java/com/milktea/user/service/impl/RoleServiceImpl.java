package com.milktea.user.service.impl;

import com.milktea.model.entity.Menu;
import com.milktea.model.entity.Role;
import com.milktea.user.mapper.MenuMapper;
import com.milktea.user.mapper.RoleMapper;
import com.milktea.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Role> list() {
        return roleMapper.findAll();
    }

    @Override
    public Role queryById(Long id) {
        Role role = roleMapper.findById(id);
        if (role != null) {
            role.setMenuIds(roleMapper.findMenuIdsByRoleId(id));
        }
        return role;
    }

    @Override
    public void add(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.update(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        roleMapper.deleteRoleMenus(id); // 级联删 role_menu
        roleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMapper.deleteRoleMenus(roleId); // 先删
        if (menuIds != null && !menuIds.isEmpty()) {
            roleMapper.insertRoleMenus(roleId, menuIds); // 后插
        }
    }

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> all = menuMapper.findAll();
        // 找到顶级菜单（parentId=0），递归组装 children
        return buildTree(all, 0L);
    }

    private List<Menu> buildTree(List<Menu> all, Long parentId) {
        return all.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .peek(m -> m.setChildren(buildTree(all, m.getId())))
                .collect(Collectors.toList());
    }
}