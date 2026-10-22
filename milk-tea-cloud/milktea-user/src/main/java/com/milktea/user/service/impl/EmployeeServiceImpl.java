package com.milktea.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.milktea.common.exception.BusinessException;
import com.milktea.common.result.PageResult;
import com.milktea.model.dto.EmployeeDTO;
import com.milktea.model.entity.Employee;
import com.milktea.model.entity.User;
import com.milktea.model.vo.EmployeeVO;
import com.milktea.user.mapper.EmployeeMapper;
import com.milktea.user.mapper.RoleMapper;
import com.milktea.user.mapper.UserMapper;
import com.milktea.user.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public PageResult<EmployeeVO> page(Integer pageNum, Integer pageSize, String keyword) {
        PageHelper.startPage(pageNum, pageSize);
        List<EmployeeVO> list = employeeMapper.page(keyword);
        return PageResult.of(list);
    }

    @Override
    public EmployeeVO queryById(Long id) {
        return employeeMapper.findById(id);
    }

    @Override
    @Transactional
    public void add(EmployeeDTO dto) {
        // 1. 建 user（密码默认 123456）
        User user = new User();
        user.setUsername(dto.getEmployeeNo());
        user.setPhone(dto.getPhone());
        String pwd = (dto.getPassword() != null && !dto.getPassword().isEmpty())
                ? dto.getPassword() : "123456";
        user.setPassword(passwordEncoder.encode(pwd));
        user.setNickname(dto.getRealName());
        user.setUserType(1); // 1=员工
        userMapper.insert(user);

        // 2. 建 employee
        Employee emp = new Employee();
        emp.setUserId(user.getId());
        emp.setEmployeeNo(dto.getEmployeeNo());
        emp.setRealName(dto.getRealName());
        emp.setGender(dto.getGender());
        emp.setPosition(dto.getPosition());
        employeeMapper.insert(emp);

        // 3. 建 user_role
        if (dto.getRoleId() != null) {
            roleMapper.insertUserRole(user.getId(), dto.getRoleId());
        }
    }

    @Override
    @Transactional
    public void update(EmployeeDTO dto) {
        // 1. 更新 employee
        Employee emp = new Employee();
        emp.setId(dto.getId());
        emp.setEmployeeNo(dto.getEmployeeNo());
        emp.setRealName(dto.getRealName());
        emp.setGender(dto.getGender());
        emp.setPosition(dto.getPosition());
        employeeMapper.update(emp);

        // 2. 更新 user 的 phone
        Long userId = employeeMapper.findUserIdById(dto.getId());
        if (userId != null && dto.getPhone() != null) {
            User user = new User();
            user.setId(userId);
            user.setPhone(dto.getPhone());
            userMapper.updateInfo(userId, null, null); // 不改昵称头像
        }

        // 3. 角色变更：先删后插
        if (userId != null && dto.getRoleId() != null) {
            roleMapper.deleteUserRoles(userId);
            roleMapper.insertUserRole(userId, dto.getRoleId());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long userId = employeeMapper.findUserIdById(id);
        if (userId != null) {
            roleMapper.deleteUserRoles(userId); // 级联删 user_role
        }
        employeeMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        employeeMapper.updateStatus(id, status);
    }
}