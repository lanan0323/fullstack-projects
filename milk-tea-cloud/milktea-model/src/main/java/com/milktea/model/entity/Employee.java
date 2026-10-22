package com.milktea.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Employee {
    private Long id;
    private Long userId;
    private String employeeNo;
    private String realName;
    private Integer gender;
    private String position;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
