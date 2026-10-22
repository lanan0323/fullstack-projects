package com.milktea.model.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String phone;
    private String password;
    private String nickname;
}
