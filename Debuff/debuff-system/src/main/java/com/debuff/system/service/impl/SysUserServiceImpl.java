// 包声明
package com.debuff.system.service.impl;

// 导入相关依赖
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.debuff.common.annotation.DataScope;
import com.debuff.common.constant.UserConstants;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.bean.BeanValidators;
import com.debuff.common.utils.spring.SpringUtils;
import com.debuff.system.domain.SysPost;
import com.debuff.system.domain.SysUserPost;
import com.debuff.system.domain.SysUserRole;
import com.debuff.system.mapper.SysPostMapper;
import com.debuff.system.mapper.SysRoleMapper;
import com.debuff.system.mapper.SysUserMapper;
import com.debuff.system.mapper.SysUserPostMapper;
import com.debuff.system.mapper.SysUserRoleMapper;
import com.debuff.system.service.ISysConfigService;
import com.debuff.system.service.ISysUserService;

/**
 * 用户 业务层处理
 * 
 * @author ruoyi
 */
@Service // 注册为Spring服务Bean
public class SysUserServiceImpl implements ISysUserService
{
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class); // 日志记录器

    @Autowired // 自动注入用户Mapper
    private SysUserMapper userMapper;

    @Autowired // 自动注入角色Mapper
    private SysRoleMapper roleMapper;

    @Autowired // 自动注入岗位Mapper
    private SysPostMapper postMapper;

    @Autowired // 自动注入用户角色关联Mapper
    private SysUserRoleMapper userRoleMapper;

    @Autowired // 自动注入用户岗位关联Mapper
    private SysUserPostMapper userPostMapper;

    @Autowired // 自动注入参数配置服务
    private ISysConfigService configService;

    @Autowired // 自动注入Bean验证器
    protected Validator validator;

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u") // 数据权限过滤，部门别名d，用户别名u
    public List<SysUser> selectUserList(SysUser user)
    {
        return userMapper.selectUserList(user); // 条件查询用户列表
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u") // 数据权限过滤
    public List<SysUser> selectAllocatedList(SysUser user)
    {
        return userMapper.selectAllocatedList(user); // 查询已分配角色的用户列表
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u") // 数据权限过滤
    public List<SysUser> selectUnallocatedList(SysUser user)
    {
        return userMapper.selectUnallocatedList(user); // 查询未分配角色的用户列表
    }

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return userMapper.selectUserByUserName(userName); // 根据用户名查询用户
    }

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId); // 根据ID查询用户详情
    }

    /**
     * 查询用户所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName); // 查询用户关联的角色列表
        if (CollectionUtils.isEmpty(list)) // 角色列表为空
        {
            return StringUtils.EMPTY; // 返回空字符串
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(",")); // 角色名用逗号拼接返回
    }

    /**
     * 查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName)
    {
        List<SysPost> list = postMapper.selectPostsByUserName(userName); // 查询用户关联的岗位列表
        if (CollectionUtils.isEmpty(list)) // 岗位列表为空
        {
            return StringUtils.EMPTY; // 返回空字符串
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(",")); // 岗位名用逗号拼接返回
    }

    /**
     * 校验用户名称是否唯一
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId(); // 获取用户ID，新增时为-1
        SysUser info = userMapper.checkUserNameUnique(user.getUserName()); // 根据用户名查询用户
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE; // 用户名已存在，不唯一
        }
        return UserConstants.UNIQUE; // 用户名唯一
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId(); // 获取用户ID，新增时为-1
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber()); // 根据手机号查询用户
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE; // 手机号已存在，不唯一
        }
        return UserConstants.UNIQUE; // 手机号唯一
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId(); // 获取用户ID，新增时为-1
        SysUser info = userMapper.checkEmailUnique(user.getEmail()); // 根据邮箱查询用户
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE; // 邮箱已存在，不唯一
        }
        return UserConstants.UNIQUE; // 邮箱唯一
    }

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员用户"); // 禁止操作超级管理员
        }
    }

    /**
     * 校验用户是否有数据权限
     * 
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId)
    {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) // 非管理员才校验数据权限
        {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user); // 通过AOP代理查询以触发数据权限
            if (StringUtils.isEmpty(users))
            {
                throw new ServiceException("没有权限访问用户数据！"); // 无数据权限则抛异常
            }
        }
    }

    /**
     * 新增保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional // 开启事务，保证用户、岗位、角色一起提交
    public int insertUser(SysUser user)
    {
        // 新增用户信息
        int rows = userMapper.insertUser(user); // 插入用户基本信息
        // 新增用户岗位关联
        insertUserPost(user); // 插入用户岗位关联
        // 新增用户与角色管理
        insertUserRole(user); // 插入用户角色关联
        return rows; // 返回影响行数
    }

    /**
     * 注册用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user)
    {
        return userMapper.insertUser(user) > 0; // 插入用户并判断是否成功
    }

    /**
     * 修改保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional // 开启事务
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId(); // 获取用户ID
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId); // 先删除旧的角色关联
        // 新增用户与角色管理
        insertUserRole(user); // 重新插入角色关联
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId); // 先删除旧的岗位关联
        // 新增用户与岗位管理
        insertUserPost(user); // 重新插入岗位关联
        return userMapper.updateUser(user); // 更新用户基本信息
    }

    /**
     * 用户授权角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional // 开启事务
    public void insertUserAuth(Long userId, Long[] roleIds)
    {
        userRoleMapper.deleteUserRoleByUserId(userId); // 先删除旧的角色关联
        insertUserRole(userId, roleIds); // 重新插入角色关联
    }

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user)
    {
        return userMapper.updateUser(user); // 更新用户状态
    }

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user)
    {
        return userMapper.updateUser(user); // 更新用户个人资料
    }

    /**
     * 修改用户头像
     * 
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar)
    {
        return userMapper.updateUserAvatar(userName, avatar) > 0; // 更新头像并判断是否成功
    }

    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user)
    {
        return userMapper.updateUser(user); // 更新用户密码
    }

    /**
     * 重置用户密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password)
    {
        return userMapper.resetUserPwd(userName, password); // 根据用户名重置密码
    }

    /**
     * 新增用户角色信息
     * 
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        this.insertUserRole(user.getUserId(), user.getRoleIds()); // 调用重载方法插入用户角色关联
    }

    /**
     * 新增用户岗位信息
     * 
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user)
    {
        Long[] posts = user.getPostIds(); // 获取岗位ID数组
        if (StringUtils.isNotEmpty(posts)) // 岗位ID不为空
        {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length); // 构建用户岗位关联列表
            for (Long postId : posts) // 遍历岗位ID
            {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId()); // 设置用户ID
                up.setPostId(postId); // 设置岗位ID
                list.add(up); // 加入关联列表
            }
            userPostMapper.batchUserPost(list); // 批量插入用户岗位关联
        }
    }

    /**
     * 新增用户角色信息
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds)
    {
        if (StringUtils.isNotEmpty(roleIds)) // 角色ID不为空
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length); // 构建用户角色关联列表
            for (Long roleId : roleIds) // 遍历角色ID
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId); // 设置用户ID
                ur.setRoleId(roleId); // 设置角色ID
                list.add(ur); // 加入关联列表
            }
            userRoleMapper.batchUserRole(list); // 批量插入用户角色关联
        }
    }

    /**
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional // 开启事务
    public int deleteUserById(Long userId)
    {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId); // 删除用户角色关联
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId); // 删除用户岗位关联
        return userMapper.deleteUserById(userId); // 删除用户本身
    }

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional // 开启事务
    public int deleteUserByIds(Long[] userIds)
    {
        for (Long userId : userIds) // 遍历每个待删除的用户
        {
            checkUserAllowed(new SysUser(userId)); // 校验是否允许操作（超级管理员不可删）
            checkUserDataScope(userId); // 校验数据权限
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds); // 批量删除用户角色关联
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds); // 批量删除用户岗位关联
        return userMapper.deleteUserByIds(userIds); // 批量删除用户本身
    }

    /**
     * 导入用户数据
     * 
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(userList) || userList.size() == 0) // 导入数据为空
        {
            throw new ServiceException("导入用户数据不能为空！"); // 抛出异常
        }
        int successNum = 0; // 成功计数
        int failureNum = 0; // 失败计数
        StringBuilder successMsg = new StringBuilder(); // 成功消息
        StringBuilder failureMsg = new StringBuilder(); // 失败消息
        String password = configService.selectConfigByKey("sys.user.initPassword"); // 从配置表获取初始密码
        for (SysUser user : userList) // 遍历每个待导入的用户
        {
            try
            {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName()); // 根据用户名查询用户
                if (StringUtils.isNull(u)) // 用户不存在，新增
                {
                    BeanValidators.validateWithException(validator, user); // Bean校验用户数据
                    user.setPassword(SecurityUtils.encryptPassword(password)); // 加密初始密码
                    user.setCreateBy(operName); // 设置创建者
                    userMapper.insertUser(user); // 插入新用户
                    successNum++; // 成功数+1
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功"); // 记录成功消息
                }
                else if (isUpdateSupport) // 用户已存在且支持更新
                {
                    BeanValidators.validateWithException(validator, user); // Bean校验用户数据
                    checkUserAllowed(u); // 校验是否允许操作
                    checkUserDataScope(u.getUserId()); // 校验数据权限
                    user.setUserId(u.getUserId()); // 设置用户ID
                    user.setUpdateBy(operName); // 设置更新者
                    userMapper.updateUser(user); // 更新用户信息
                    successNum++; // 成功数+1
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功"); // 记录成功消息
                }
                else // 用户已存在且不支持更新
                {
                    failureNum++; // 失败数+1
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在"); // 记录失败消息
                }
            }
            catch (Exception e) // 捕获导入异常
            {
                failureNum++; // 失败数+1
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage()); // 记录失败原因
                log.error(msg, e); // 记录错误日志
            }
        }
        if (failureNum > 0) // 存在失败记录
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下："); // 拼接失败汇总消息
            throw new ServiceException(failureMsg.toString()); // 抛出异常
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下："); // 拼接成功汇总消息
        }
        return successMsg.toString(); // 返回成功消息
    }
}
