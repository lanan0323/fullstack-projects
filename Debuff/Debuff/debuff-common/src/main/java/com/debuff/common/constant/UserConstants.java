// 包声明：用户常量所在包
package com.debuff.common.constant;

/**
 * 用户常量信息
 * 
 * @author ruoyi
 */
public class UserConstants
{
    /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER"; // 系统用户标识

    /** 正常状态 */
    public static final String NORMAL = "0"; // 正常状态值

    /** 异常状态 */
    public static final String EXCEPTION = "1"; // 异常状态值

    /** 用户封禁状态 */
    public static final String USER_DISABLE = "1"; // 用户被禁用

    /** 角色封禁状态 */
    public static final String ROLE_DISABLE = "1"; // 角色被禁用

    /** 部门正常状态 */
    public static final String DEPT_NORMAL = "0"; // 部门正常状态值

    /** 部门停用状态 */
    public static final String DEPT_DISABLE = "1"; // 部门停用状态值

    /** 字典正常状态 */
    public static final String DICT_NORMAL = "0"; // 字典正常状态值

    /** 是否为系统默认（是） */
    public static final String YES = "Y"; // 是标识

    /** 是否菜单外链（是） */
    public static final String YES_FRAME = "0"; // 是外链标识

    /** 是否菜单外链（否） */
    public static final String NO_FRAME = "1"; // 非外链标识

    /** 菜单类型（目录） */
    public static final String TYPE_DIR = "M"; // 目录类型菜单

    /** 菜单类型（菜单） */
    public static final String TYPE_MENU = "C"; // 菜单类型

    /** 菜单类型（按钮） */
    public static final String TYPE_BUTTON = "F"; // 按钮类型

    /** Layout组件标识 */
    public final static String LAYOUT = "Layout"; // 前端Layout布局组件名

    /** ParentView组件标识 */
    public final static String PARENT_VIEW = "ParentView"; // 前端父视图组件名

    /** InnerLink组件标识 */
    public final static String INNER_LINK = "InnerLink"; // 前端内链组件名

    /** 校验是否唯一的返回标识 */
    public final static boolean UNIQUE = true; // 唯一（校验通过）
    public final static boolean NOT_UNIQUE = false; // 不唯一（校验不通过）

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2; // 用户名最小长度
    public static final int USERNAME_MAX_LENGTH = 20; // 用户名最大长度

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 5; // 密码最小长度
    public static final int PASSWORD_MAX_LENGTH = 20; // 密码最大长度
}
