package com.milktea.model.dto;

import lombok.Data;

@Data
public class EmployeeDTO {
    private Long id;
    private Long userId;
    private String employeeNo;
    private String realName;
    private Integer gender;
    private String position;
    private String phone;
    private String password;
    private Long roleId;
}
