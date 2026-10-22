package com.milktea.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private Integer userType;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
