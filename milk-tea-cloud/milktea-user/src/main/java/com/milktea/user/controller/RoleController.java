package com.milktea.user.controller;

import com.milktea.common.result.Result;
import com.milktea.model.entity.Menu;
import com.milktea.model.entity.Role;
import com.milktea.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(roleService.list());
    }

    @GetMapping("/{id}")
    public Result<Role> queryById(@PathVariable Long id) {
        return Result.success(roleService.queryById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Role role) {
        roleService.add(role);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        roleService.update(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success();
    }

    @GetMapping("/menu/tree")
    public Result<List<Menu>> getMenuTree() {
        return Result.success(roleService.getMenuTree());
    }
}