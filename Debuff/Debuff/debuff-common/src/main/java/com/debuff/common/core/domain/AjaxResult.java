// 包声明：核心域对象所在包
package com.debuff.common.core.domain;

// 导入HashMap用于存储键值对
import java.util.HashMap;
// 导入Objects工具类用于比较
import java.util.Objects;
// 导入HTTP状态码常量
import com.debuff.common.constant.HttpStatus;
// 导入字符串工具类
import com.debuff.common.utils.StringUtils;

/**
 * 操作消息提醒
 * 
 * @author ruoyi
 */
public class AjaxResult extends HashMap<String, Object> // 继承HashMap，以键值对形式存储响应数据
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 状态码 */
    public static final String CODE_TAG = "code"; // 响应中状态码的key

    /** 返回内容 */
    public static final String MSG_TAG = "msg"; // 响应中消息的key

    /** 数据对象 */
    public static final String DATA_TAG = "data"; // 响应中数据的key

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult() // 无参构造方法
    {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg) // 构造方法，设置状态码和消息
    {
        super.put(CODE_TAG, code); // 存入状态码
        super.put(MSG_TAG, msg); // 存入消息
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data) // 构造方法，设置状态码、消息和数据
    {
        super.put(CODE_TAG, code); // 存入状态码
        super.put(MSG_TAG, msg); // 存入消息
        if (StringUtils.isNotNull(data)) // 数据不为空时才放入
        {
            super.put(DATA_TAG, data); // 存入数据对象
        }
    }

    /**
     * 返回成功消息
     * 
     * @return 成功消息
     */
    public static AjaxResult success() // 返回默认成功消息
    {
        return AjaxResult.success("操作成功"); // 调用带消息的成功方法
    }

    /**
     * 返回成功数据
     * 
     * @return 成功消息
     */
    public static AjaxResult success(Object data) // 返回成功消息并携带数据
    {
        return AjaxResult.success("操作成功", data); // 调用带消息和数据的成功方法
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg) // 返回指定消息的成功响应
    {
        return AjaxResult.success(msg, null); // 调用带消息的成功方法，数据为空
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data) // 返回指定消息和数据的成功响应
    {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data); // 创建200状态码的成功响应
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult warn(String msg) // 返回指定消息的警告响应
    {
        return AjaxResult.warn(msg, null); // 调用带消息的警告方法，数据为空
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult warn(String msg, Object data) // 返回指定消息和数据的警告响应
    {
        return new AjaxResult(HttpStatus.WARN, msg, data); // 创建601状态码的警告响应
    }

    /**
     * 返回错误消息
     * 
     * @return 错误消息
     */
    public static AjaxResult error() // 返回默认错误消息
    {
        return AjaxResult.error("操作失败"); // 调用带消息的错误方法
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @return 错误消息
     */
    public static AjaxResult error(String msg) // 返回指定消息的错误响应
    {
        return AjaxResult.error(msg, null); // 调用带消息的错误方法，数据为空
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 错误消息
     */
    public static AjaxResult error(String msg, Object data) // 返回指定消息和数据的错误响应
    {
        return new AjaxResult(HttpStatus.ERROR, msg, data); // 创建500状态码的错误响应
    }

    /**
     * 返回错误消息
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @return 错误消息
     */
    public static AjaxResult error(int code, String msg) // 返回自定义状态码和消息的错误响应
    {
        return new AjaxResult(code, msg, null); // 创建自定义状态码的错误响应
    }

    /**
     * 是否为成功消息
     *
     * @return 结果
     */
    public boolean isSuccess() // 判断响应是否为成功状态
    {
        return Objects.equals(HttpStatus.SUCCESS, this.get(CODE_TAG)); // 比较状态码是否为200
    }

    /**
     * 是否为警告消息
     *
     * @return 结果
     */
    public boolean isWarn() // 判断响应是否为警告状态
    {
        return Objects.equals(HttpStatus.WARN, this.get(CODE_TAG)); // 比较状态码是否为601
    }

    /**
     * 是否为错误消息
     *
     * @return 结果
     */
    public boolean isError() // 判断响应是否为错误状态
    {
        return Objects.equals(HttpStatus.ERROR, this.get(CODE_TAG)); // 比较状态码是否为500
    }

    /**
     * 方便链式调用
     *
     * @param key 键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public AjaxResult put(String key, Object value) // 重写put方法，支持链式调用
    {
        super.put(key, value); // 调用父类put存入键值对
        return this; // 返回自身对象，支持链式调用
    }
}
