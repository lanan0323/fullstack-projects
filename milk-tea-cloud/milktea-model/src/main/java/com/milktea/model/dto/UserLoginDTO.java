package com.milktea.model.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String username;
    private String phone;
    private String password;
}
