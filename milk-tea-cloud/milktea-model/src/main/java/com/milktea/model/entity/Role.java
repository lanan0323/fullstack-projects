package com.milktea.model.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Role {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Long> menuIds;
}
