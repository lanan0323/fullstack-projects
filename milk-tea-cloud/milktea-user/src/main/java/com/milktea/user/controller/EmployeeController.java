package com.milktea.user.controller;

import com.milktea.common.result.PageResult;
import com.milktea.common.result.Result;
import com.milktea.model.dto.EmployeeDTO;
import com.milktea.model.vo.EmployeeVO;
import com.milktea.user.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/page")
    public Result<PageResult<EmployeeVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(employeeService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<EmployeeVO> queryById(@PathVariable Long id) {
        return Result.success(employeeService.queryById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody EmployeeDTO dto) {
        employeeService.add(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody EmployeeDTO dto) {
        employeeService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        employeeService.updateStatus(id, status);
        return Result.success();
    }
}