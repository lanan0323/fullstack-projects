package com.milktea.user.service;

import com.milktea.model.dto.UserLoginDTO;
import com.milktea.model.dto.UserRegisterDTO;
import com.milktea.model.entity.User;
import com.milktea.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserVO login(UserLoginDTO dto);
    void register(UserRegisterDTO dto);
    User queryById(Long id);
    void updatePassword(String oldPassword, String newPassword);
    void updateInfo(String nickname, String avatar);
    String uploadAvatar(MultipartFile file);
}
