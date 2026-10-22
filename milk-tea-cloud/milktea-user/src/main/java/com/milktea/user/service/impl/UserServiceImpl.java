package com.milktea.user.service.impl;

import com.milktea.common.context.UserContext;
import com.milktea.common.exception.BusinessException;
import com.milktea.common.utils.JwtUtils;
import com.milktea.model.dto.UserLoginDTO;
import com.milktea.model.dto.UserRegisterDTO;
import com.milktea.model.entity.User;
import com.milktea.model.vo.UserVO;
import com.milktea.user.mapper.MenuMapper;
import com.milktea.user.mapper.RoleMapper;
import com.milktea.user.mapper.UserMapper;
import com.milktea.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private com.milktea.user.service.PointsService pointsService;

    @Override
    public UserVO login(UserLoginDTO dto) {
        // 登录输入框单字段兼容: 纯数字11位按手机号查, 否则按用户名查
        User user = null;
        String account = dto.getUsername();
        if (account != null && account.matches("1\\d{10}")) {
            user = userMapper.findByPhone(account);
        }
        if (user == null && dto.getPhone() != null && !dto.getPhone().isBlank()) {
            user = userMapper.findByPhone(dto.getPhone());
        }
        if (user == null) {
            user = userMapper.findByUsername(account);
        }
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        // 签发 JWT（1小时），claims 放 userId 和 userType
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("userType", user.getUserType());
        String token = JwtUtils.createJWT(3600000, claims);

        // 查角色ID和权限标识(新注册用户无角色, 跳过IN()空集查询)
        List<Long> roleIds = roleMapper.findRoleIdsByUserId(user.getId());
        List<String> permissions = (roleIds == null || roleIds.isEmpty())
                ? new java.util.ArrayList<>() : menuMapper.findPermissionsByRoleIds(roleIds);

        // 组装 UserVO 返回
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .userType(user.getUserType())
                .token(token)
                .roleIds(roleIds)
                .permissions(permissions)
                .build();
    }

    @Override
    public void register(UserRegisterDTO dto) {
        // 校验手机号未注册
        if (userMapper.countByPhone(dto.getPhone()) > 0) {
            throw new BusinessException("该手机号已注册");
        }
        User user = new User();
        user.setUsername(dto.getPhone()); // 用户名默认取手机号, 保证注册后可直接登录
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setUserType(0); // 0=顾客
        userMapper.insert(user);
        // 新人注册礼包: 送100积分
        pointsService.earn(user.getId(), 100, "新人注册礼包");
    }

    @Override
    public User queryById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getCurrentUserId();
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        userMapper.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    public void updateInfo(String nickname, String avatar) {
        Long userId = UserContext.getCurrentUserId();
        userMapper.updateInfo(userId, nickname, avatar);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        Long userId = UserContext.getCurrentUserId();
        if (file == null || file.isEmpty()) throw new BusinessException("请选择图片");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) throw new BusinessException("仅支持图片格式");
        if (file.getSize() > 2 * 1024 * 1024) throw new BusinessException("图片不能超过2MB");
        String ext = contentType.contains("png") ? ".png" : contentType.contains("webp") ? ".webp" : ".jpg";
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get("D:/milktea-upload");
            java.nio.file.Files.createDirectories(dir);
            String name = java.util.UUID.randomUUID().toString().replace("-", "") + ext;
            file.transferTo(dir.resolve(name).toFile());
            String url = "http://localhost:8081/uploads/" + name;
            userMapper.updateAvatar(userId, url);
            return url;
        } catch (java.io.IOException e) {
            throw new BusinessException("上传失败, 请重试");
        }
    }
}