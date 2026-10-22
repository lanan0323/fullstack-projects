// 包声明：HTTP状态码常量所在包
package com.debuff.common.constant;

/**
 * 返回状态码
 * 
 * @author ruoyi
 */
public class HttpStatus
{
    /**
     * 操作成功
     */
    public static final int SUCCESS = 200; // 请求成功

    /**
     * 对象创建成功
     */
    public static final int CREATED = 201; // 资源创建成功

    /**
     * 请求已经被接受
     */
    public static final int ACCEPTED = 202; // 请求已接受但尚未处理完成

    /**
     * 操作已经执行成功，但是没有返回数据
     */
    public static final int NO_CONTENT = 204; // 成功但无返回内容

    /**
     * 资源已被移除
     */
    public static final int MOVED_PERM = 301; // 资源永久移动（重定向）

    /**
     * 重定向
     */
    public static final int SEE_OTHER = 303; // 重定向到其他资源

    /**
     * 资源没有被修改
     */
    public static final int NOT_MODIFIED = 304; // 资源未修改，可使用缓存

    /**
     * 参数列表错误（缺少，格式不匹配）
     */
    public static final int BAD_REQUEST = 400; // 请求参数错误

    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401; // 未认证，需要登录

    /**
     * 访问受限，授权过期
     */
    public static final int FORBIDDEN = 403; // 无权限访问

    /**
     * 资源，服务未找到
     */
    public static final int NOT_FOUND = 404; // 请求的资源不存在

    /**
     * 不允许的http方法
     */
    public static final int BAD_METHOD = 405; // HTTP方法不被允许

    /**
     * 资源冲突，或者资源被锁
     */
    public static final int CONFLICT = 409; // 资源冲突

    /**
     * 不支持的数据，媒体类型
     */
    public static final int UNSUPPORTED_TYPE = 415; // 不支持的媒体类型

    /**
     * 系统内部错误
     */
    public static final int ERROR = 500; // 服务器内部错误

    /**
     * 接口未实现
     */
    public static final int NOT_IMPLEMENTED = 501; // 功能未实现

    /**
     * 系统警告消息
     */
    public static final int WARN = 601; // 自定义警告状态码
}
