// 包声明
package com.debuff.framework.aspectj;

// 导入所需类
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.debuff.common.annotation.DataScope;
import com.debuff.common.core.domain.BaseEntity;
import com.debuff.common.core.domain.entity.SysRole;
import com.debuff.common.core.domain.entity.SysUser;
import com.debuff.common.core.domain.model.LoginUser;
import com.debuff.common.core.text.Convert;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.framework.security.context.PermissionContextHolder;

/**
 * 数据过滤处理
 *
 * @author ruoyi
 */
@Aspect // 声明为AOP切面
@Component // 注册为Spring Bean
public class DataScopeAspect
{
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1"; // 全部数据权限常量值

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2"; // 自定义数据权限常量值

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3"; // 部门数据权限常量值

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4"; // 部门及子部门数据权限常量值

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5"; // 仅本人数据权限常量值

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope"; // 存储在BaseEntity.params中的SQL过滤条件key

    @Before("@annotation(controllerDataScope)") // 前置通知，拦截标注了@DataScope的方法
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point); // 先清空已有的数据权限参数，防止SQL注入
        handleDataScope(point, controllerDataScope); // 处理数据权限过滤逻辑
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) // 处理数据权限的核心入口方法
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser(); // 从Security上下文获取当前登录用户
        if (StringUtils.isNotNull(loginUser)) // 如果用户已登录
        {
            SysUser currentUser = loginUser.getUser(); // 获取当前用户对象
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) // 非超级管理员才进行数据过滤
            {
                String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), PermissionContextHolder.getContext()); // 获取权限标识，优先用注解值
                dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(), // 执行数据范围过滤
                        controllerDataScope.userAlias(), permission);
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     * @param permission 权限字符
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias, String permission) // 根据用户角色拼接数据权限SQL
    {
        StringBuilder sqlString = new StringBuilder(); // 用于拼接SQL过滤条件
        List<String> conditions = new ArrayList<String>(); // 记录已处理的数据权限类型，避免重复

        for (SysRole role : user.getRoles()) // 遍历用户的所有角色
        {
            String dataScope = role.getDataScope(); // 获取当前角色的数据权限类型
            if (!DATA_SCOPE_CUSTOM.equals(dataScope) && conditions.contains(dataScope)) // 非自定义权限且已处理过则跳过
            {
                continue;
            }
            if (StringUtils.isNotEmpty(permission) && StringUtils.isNotEmpty(role.getPermissions()) // 如果角色有权限标识且不包含当前所需权限则跳过
                    && !StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission)))
            {
                continue;
            }
            if (DATA_SCOPE_ALL.equals(dataScope)) // 如果是全部数据权限
            {
                sqlString = new StringBuilder(); // 清空之前拼接的条件
                conditions.add(dataScope); // 记录已处理
                break; // 全部权限无需再过滤，直接跳出
            }
            else if (DATA_SCOPE_CUSTOM.equals(dataScope)) // 如果是自定义数据权限
            {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias, // 通过角色-部门关联表查询自定义部门
                        role.getRoleId()));
            }
            else if (DATA_SCOPE_DEPT.equals(dataScope)) // 如果是部门数据权限
            {
                sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId())); // 只查询本部门数据
            }
            else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) // 如果是部门及子部门数据权限
            {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )", // 利用find_in_set查询本部门及所有子部门
                        deptAlias, user.getDeptId(), user.getDeptId()));
            }
            else if (DATA_SCOPE_SELF.equals(dataScope)) // 如果是仅本人数据权限
            {
                if (StringUtils.isNotBlank(userAlias)) // 如果指定了用户别名
                {
                    sqlString.append(StringUtils.format(" OR {}.user_id = {} ", userAlias, user.getUserId())); // 只查询本人数据
                }
                else
                {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias)); // 无用户别名时设置不可能匹配的条件
                }
            }
            conditions.add(dataScope); // 记录该权限类型已处理
        }

        // 多角色情况下，所有角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (StringUtils.isEmpty(conditions)) // 如果没有匹配到任何权限条件
        {
            sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias)); // 设置不可能匹配的条件，不返回任何数据
        }

        if (StringUtils.isNotBlank(sqlString.toString())) // 如果拼接了有效的SQL条件
        {
            Object params = joinPoint.getArgs()[0]; // 获取方法的第一个参数
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity) // 参数必须是BaseEntity类型
            {
                BaseEntity baseEntity = (BaseEntity) params; // 转换为BaseEntity
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")"); // 将过滤SQL存入params中，substring(4)去掉开头的" OR "
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) // 清空数据权限参数，防止前端传入恶意SQL
    {
        Object params = joinPoint.getArgs()[0]; // 获取方法第一个参数
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity) // 如果是BaseEntity类型
        {
            BaseEntity baseEntity = (BaseEntity) params; // 转换类型
            baseEntity.getParams().put(DATA_SCOPE, ""); // 清空dataScope参数
        }
    }
}
