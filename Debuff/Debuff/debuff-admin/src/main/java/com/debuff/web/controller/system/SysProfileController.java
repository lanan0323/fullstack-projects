// 包声明
package com.debuff.web.controller.system;

// 导入所需类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.debuff.common.annotation.Log;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.core.controller.BaseController;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.enums.BusinessType;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.file.FileUploadUtils;
import com.debuff.common.utils.file.MimeTypeUtils;
import com.debuff.framework.web.service.TokenService;
import com.debuff.system.service.ISysUserService;

/**
 * 个人信息 业务处理
 * 
 * @author ruoyi
 */
@RestController // 标记为REST控制器
@RequestMapping("/system/user/profile") // 请求路径前缀
public class SysProfileController extends BaseController
{
    @Autowired // 自动注入用户服务
    private ISysUserService userService;

    @Autowired // 自动注入Token服务
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping // GET请求
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser(); // 获取当前登录用户
        SysUser user = loginUser.getUser(); // 获取用户实体
        AjaxResult ajax = AjaxResult.success(user); // 返回用户信息
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername())); // 查询用户所属角色组
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername())); // 查询用户所属岗位组
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE) // 记录操作日志
    @PutMapping // PUT请求
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = getLoginUser(); // 获取当前登录用户
        SysUser currentUser = loginUser.getUser(); // 获取用户实体
        currentUser.setNickName(user.getNickName()); // 设置昵称
        currentUser.setEmail(user.getEmail()); // 设置邮箱
        currentUser.setPhonenumber(user.getPhonenumber()); // 设置手机号
        currentUser.setSex(user.getSex()); // 设置性别
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser)) // 手机号唯一性校验
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser)) // 邮箱唯一性校验
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) > 0) // 更新用户资料
        {
            // 更新缓存用户信息
            tokenService.setLoginUser(loginUser); // 刷新Token中的用户信息
            return success();
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE) // 记录操作日志
    @PutMapping("/updatePwd") // PUT请求
    public AjaxResult updatePwd(String oldPassword, String newPassword)
    {
        LoginUser loginUser = getLoginUser(); // 获取当前登录用户
        String userName = loginUser.getUsername(); // 获取用户名
        String password = loginUser.getPassword(); // 获取当前密码哈希
        if (!SecurityUtils.matchesPassword(oldPassword, password)) // 验证旧密码是否正确
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) // 新密码不能与旧密码相同
        {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword); // 加密新密码
        if (userService.resetUserPwd(userName, newPassword) > 0) // 重置用户密码
        {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(newPassword); // 更新用户实体中的密码
            tokenService.setLoginUser(loginUser); // 刷新Token
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE) // 记录操作日志
    @PostMapping("/avatar") // POST请求
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty()) // 文件不为空
        {
            LoginUser loginUser = getLoginUser(); // 获取当前登录用户
            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION); // 上传头像文件
            if (userService.updateUserAvatar(loginUser.getUsername(), avatar)) // 更新数据库中的头像路径
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatar); // 返回头像URL
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(avatar); // 更新用户实体中的头像
                tokenService.setLoginUser(loginUser); // 刷新Token
                return ajax;
            }
        }
        return error("上传图片异常，请联系管理员");
    }
}
