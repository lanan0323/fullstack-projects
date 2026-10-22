package com.milktea.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmployeeVO {
    private Long id;
    private Long userId;
    private String employeeNo;
    private String realName;
    private Integer gender;
    private String position;
    private String phone;
    private String roleName;
    private Long roleId;
    private Integer status;
    private LocalDateTime createTime;
}
