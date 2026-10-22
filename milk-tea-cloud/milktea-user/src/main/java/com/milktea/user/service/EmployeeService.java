package com.milktea.user.service;

import com.milktea.model.dto.EmployeeDTO;
import com.milktea.model.vo.EmployeeVO;
import com.milktea.common.result.PageResult;

public interface EmployeeService {
    PageResult<EmployeeVO> page(Integer pageNum, Integer pageSize, String keyword);
    EmployeeVO queryById(Long id);
    void add(EmployeeDTO dto);
    void update(EmployeeDTO dto);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}