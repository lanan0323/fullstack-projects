// 包声明：登录模型对象所在包
package com.debuff.common.core.domain.model;

// 导入fastjson的JSONField注解，控制序列化
import com.alibaba.fastjson2.annotation.JSONField;
// 导入用户实体类
import com.debuff.common.core.domain.entity.SysUser;
// 导入Spring Security的权限接口
import org.springframework.security.core.GrantedAuthority;
// 导入Spring Security的UserDetails接口
import org.springframework.security.core.userdetails.UserDetails;
// 导入集合类
import java.util.Collection;
import java.util.Set;

/**
 * 登录用户身份权限
 * 
 * @author ruoyi
 */
public class LoginUser implements UserDetails // 实现Spring Security的UserDetails接口
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /**
     * 用户ID
     */
    private Long userId; // 用户ID

    /**
     * 部门ID
     */
    private Long deptId; // 所属部门ID

    /**
     * 用户唯一标识
     */
    private String token; // 登录令牌（唯一标识）

    /**
     * 登录时间
     */
    private Long loginTime; // 登录时间戳

    /**
     * 过期时间
     */
    private Long expireTime; // 令牌过期时间戳

    /**
     * 登录IP地址
     */
    private String ipaddr; // 登录IP地址

    /**
     * 登录地点
     */
    private String loginLocation; // 登录地理位置

    /**
     * 浏览器类型
     */
    private String browser; // 浏览器类型

    /**
     * 操作系统
     */
    private String os; // 操作系统

    /**
     * 权限列表
     */
    private Set<String> permissions; // 用户权限标识集合

    /**
     * 用户信息
     */
    private SysUser user; // 关联的用户实体对象

    public LoginUser() // 无参构造方法
    {
    }

    public LoginUser(SysUser user, Set<String> permissions) // 通过用户对象和权限集合构造
    {
        this.user = user; // 设置用户对象
        this.permissions = permissions; // 设置权限集合
    }

    public LoginUser(Long userId, Long deptId, SysUser user, Set<String> permissions) // 完整参数构造
    {
        this.userId = userId; // 设置用户ID
        this.deptId = deptId; // 设置部门ID
        this.user = user; // 设置用户对象
        this.permissions = permissions; // 设置权限集合
    }

    public Long getUserId() { return userId; } // 获取用户ID

    public void setUserId(Long userId) { this.userId = userId; } // 设置用户ID

    public Long getDeptId() { return deptId; } // 获取部门ID

    public void setDeptId(Long deptId) { this.deptId = deptId; } // 设置部门ID

    public String getToken() { return token; } // 获取令牌

    public void setToken(String token) { this.token = token; } // 设置令牌

    @JSONField(serialize = false) // JSON序列化时忽略密码字段
    @Override
    public String getPassword() // 获取密码（实现UserDetails接口）
    {
        return user.getPassword(); // 从用户对象获取密码
    }

    @Override
    public String getUsername() // 获取用户名（实现UserDetails接口）
    {
        return user.getUserName(); // 从用户对象获取用户名
    }

    /**
     * 账户是否未过期,过期无法验证
     */
    @JSONField(serialize = false) // JSON序列化时忽略
    @Override
    public boolean isAccountNonExpired() // 账户是否未过期
    {
        return true; // 默认未过期
    }

    /**
     * 指定用户是否解锁,锁定的用户无法进行身份验证
     */
    @JSONField(serialize = false) // JSON序列化时忽略
    @Override
    public boolean isAccountNonLocked() // 账户是否未锁定
    {
        return true; // 默认未锁定
    }

    /**
     * 指示是否已过期的用户的凭据(密码),过期的凭据防止认证
     */
    @JSONField(serialize = false) // JSON序列化时忽略
    @Override
    public boolean isCredentialsNonExpired() // 凭据是否未过期
    {
        return true; // 默认未过期
    }

    /**
     * 是否可用 ,禁用的用户不能身份验证
     */
    @JSONField(serialize = false) // JSON序列化时忽略
    @Override
    public boolean isEnabled() // 账户是否可用
    {
        return true; // 默认可用
    }

    public Long getLoginTime() { return loginTime; } // 获取登录时间

    public void setLoginTime(Long loginTime) { this.loginTime = loginTime; } // 设置登录时间

    public String getIpaddr() { return ipaddr; } // 获取登录IP

    public void setIpaddr(String ipaddr) { this.ipaddr = ipaddr; } // 设置登录IP

    public String getLoginLocation() { return loginLocation; } // 获取登录地点

    public void setLoginLocation(String loginLocation) { this.loginLocation = loginLocation; } // 设置登录地点

    public String getBrowser() { return browser; } // 获取浏览器类型

    public void setBrowser(String browser) { this.browser = browser; } // 设置浏览器类型

    public String getOs() { return os; } // 获取操作系统

    public void setOs(String os) { this.os = os; } // 设置操作系统

    public Long getExpireTime() { return expireTime; } // 获取过期时间

    public void setExpireTime(Long expireTime) { this.expireTime = expireTime; } // 设置过期时间

    public Set<String> getPermissions() { return permissions; } // 获取权限集合

    public void setPermissions(Set<String> permissions) { this.permissions = permissions; } // 设置权限集合

    public SysUser getUser() { return user; } // 获取用户对象

    public void setUser(SysUser user) { this.user = user; } // 设置用户对象

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() // 获取授权集合（未使用）
    {
        return null; // 返回null，权限通过permissions字段管理
    }
}
