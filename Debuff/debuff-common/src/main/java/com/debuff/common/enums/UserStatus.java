// 包声明
package com.debuff.common.enums;

/**
 * 用户状态
 * 
 * @author ruoyi
 */
public enum UserStatus
{
    OK("0", "正常"), DISABLE("1", "停用"), DELETED("2", "删除"); // 枚举值:正常/停用/删除

    private final String code; // 状态编码
    private final String info; // 状态描述

    UserStatus(String code, String info) // 构造方法
    {
        this.code = code; // 设置编码
        this.info = info; // 设置描述
    }

    public String getCode() // 获取状态编码
    {
        return code;
    }

    public String getInfo() // 获取状态描述
    {
        return info;
    }
}
