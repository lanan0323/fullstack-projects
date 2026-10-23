// 包声明
package com.debuff.system.service;

// 导入集合类和用户实体类
import java.util.List;
import com.debuff.common.core.domain.entity.SysUser;

/**
 * 用户 业务层
 * 
 * @author ruoyi
 */
// 用户服务接口，定义用户的增删改查、校验、授权及导入方法
public interface ISysUserService
{
    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    // 根据条件分页查询用户列表
    public List<SysUser> selectUserList(SysUser user);

    /**
     * 根据条件分页查询已分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    // 查询已分配该角色的用户列表
    public List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    // 查询未分配该角色的用户列表
    public List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    // 根据用户名查询用户信息
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    // 根据ID查询用户详情
    public SysUser selectUserById(Long userId);

    /**
     * 根据用户ID查询用户所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    // 查询用户所属的角色名称拼接字符串
    public String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    // 查询用户所属的岗位名称拼接字符串
    public String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     * 
     * @param user 用户信息
     * @return 结果
     */
    // 检查用户名是否唯一
    public boolean checkUserNameUnique(SysUser user);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    // 检查手机号是否唯一
    public boolean checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    // 检查邮箱是否唯一
    public boolean checkEmailUnique(SysUser user);

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    // 校验当前用户是否有权操作目标用户（如不能操作超管）
    public void checkUserAllowed(SysUser user);

    /**
     * 校验用户是否有数据权限
     * 
     * @param userId 用户id
     */
    // 校验当前用户对目标用户是否有数据权限
    public void checkUserDataScope(Long userId);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    // 新增一条用户记录
    public int insertUser(SysUser user);

    /**
     * 注册用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    // 用户注册（无需管理员权限）
    public boolean registerUser(SysUser user);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    // 修改一条用户记录
    public int updateUser(SysUser user);

    /**
     * 用户授权角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    // 为用户分配角色（先删旧关联再插新关联）
    public void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    // 修改用户的启用/禁用状态
    public int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    // 修改用户个人资料信息
    public int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     * 
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    // 更新用户头像路径
    public boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    // 重置用户密码（通过用户对象）
    public int resetPwd(SysUser user);

    /**
     * 重置用户密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    // 重置用户密码（通过用户名和密码）
    public int resetUserPwd(String userName, String password);

    /**
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    // 根据ID删除单条用户
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    // 批量删除多条用户
    public int deleteUserByIds(Long[] userIds);

    /**
     * 导入用户数据
     * 
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    // 批量导入用户数据，支持新增或更新
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);
}
