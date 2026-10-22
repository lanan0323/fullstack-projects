package com.milktea.model.vo;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class UserVO {
    private Long id;
    private String username;
    private String phone;
    private String nickname;
    private String avatar;
    private Integer userType;
    private String token;
    private List<Long> roleIds;
    private List<String> permissions;
}
