package com.milktea.user.controller;

import com.milktea.common.result.Result;
import com.milktea.model.dto.UserLoginDTO;
import com.milktea.model.dto.UserRegisterDTO;
import com.milktea.model.entity.User;
import com.milktea.model.vo.UserVO;
import com.milktea.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<User> queryById(@PathVariable Long id) {
        return Result.success(userService.queryById(id));
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> body) {
        userService.updatePassword(body.get("oldPassword"), body.get("newPassword"));
        return Result.success();
    }

    @PutMapping("/info")
    public Result<Void> updateInfo(@RequestBody Map<String, String> body) {
        userService.updateInfo(body.get("nickname"), body.get("avatar"));
        return Result.success();
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return Result.success(userService.uploadAvatar(file));
    }
}