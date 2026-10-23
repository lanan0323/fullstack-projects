// 包声明
package com.debuff.quartz.task;

// 导入所需类
import org.springframework.stereotype.Component;
import com.debuff.common.utils.StringUtils;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Component("ryTask") // 注册为Spring Bean，名称为ryTask
public class RyTask // 定时任务测试类（演示用）
{
    public void ryMultipleParams(String s, Boolean b, Long l, Double d, Integer i) // 多参数测试方法
    {
        System.out.println(StringUtils.format("执行多参方法： 字符串类型{}，布尔类型{}，长整型{}，浮点型{}，整形{}", s, b, l, d, i)); // 打印参数
    }

    public void ryParams(String params) // 单参数测试方法
    {
        System.out.println("执行有参方法：" + params); // 打印参数
    }

    public void ryNoParams() // 无参数测试方法
    {
        System.out.println("执行无参方法"); // 打印提示
    }
}
