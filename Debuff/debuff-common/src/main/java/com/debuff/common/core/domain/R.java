// 包声明：核心域对象所在包
package com.debuff.common.core.domain;

// 导入Serializable接口
import java.io.Serializable;
// 导入HTTP状态码常量
import com.debuff.common.constant.HttpStatus;

/**
 * 响应信息主体
 *
 * @author ruoyi
 */
public class R<T> implements Serializable // 泛型统一响应体，替代AjaxResult的另一种封装
{
    private static final long serialVersionUID = 1L; // 序列化版本号

    /** 成功 */
    public static final int SUCCESS = HttpStatus.SUCCESS; // 成功状态码200

    /** 失败 */
    public static final int FAIL = HttpStatus.ERROR; // 失败状态码500

    private int code; // 响应状态码

    private String msg; // 响应消息

    private T data; // 响应数据（泛型）

    public static <T> R<T> ok() // 返回成功响应（无数据）
    {
        return restResult(null, SUCCESS, "操作成功"); // 调用通用构造方法
    }

    public static <T> R<T> ok(T data) // 返回成功响应并携带数据
    {
        return restResult(data, SUCCESS, "操作成功"); // 调用通用构造方法
    }

    public static <T> R<T> ok(T data, String msg) // 返回成功响应，携带数据和自定义消息
    {
        return restResult(data, SUCCESS, msg); // 调用通用构造方法
    }

    public static <T> R<T> fail() // 返回失败响应（无数据）
    {
        return restResult(null, FAIL, "操作失败"); // 调用通用构造方法
    }

    public static <T> R<T> fail(String msg) // 返回失败响应，携带自定义消息
    {
        return restResult(null, FAIL, msg); // 调用通用构造方法
    }

    public static <T> R<T> fail(T data) // 返回失败响应，携带数据
    {
        return restResult(data, FAIL, "操作失败"); // 调用通用构造方法
    }

    public static <T> R<T> fail(T data, String msg) // 返回失败响应，携带数据和自定义消息
    {
        return restResult(data, FAIL, msg); // 调用通用构造方法
    }

    public static <T> R<T> fail(int code, String msg) // 返回自定义状态码的失败响应
    {
        return restResult(null, code, msg); // 调用通用构造方法
    }

    private static <T> R<T> restResult(T data, int code, String msg) // 通用结果构造方法
    {
        R<T> apiResult = new R<>(); // 创建响应对象
        apiResult.setCode(code); // 设置状态码
        apiResult.setData(data); // 设置数据
        apiResult.setMsg(msg); // 设置消息
        return apiResult; // 返回响应对象
    }

    public int getCode() { return code; } // 获取状态码

    public void setCode(int code) { this.code = code; } // 设置状态码

    public String getMsg() { return msg; } // 获取消息

    public void setMsg(String msg) { this.msg = msg; } // 设置消息

    public T getData() { return data; } // 获取数据

    public void setData(T data) { this.data = data; } // 设置数据

    public static <T> Boolean isError(R<T> ret) // 判断响应是否为错误
    {
        return !isSuccess(ret); // 取反成功判断
    }

    public static <T> Boolean isSuccess(R<T> ret) // 判断响应是否成功
    {
        return R.SUCCESS == ret.getCode(); // 比较状态码是否为200
    }
}
