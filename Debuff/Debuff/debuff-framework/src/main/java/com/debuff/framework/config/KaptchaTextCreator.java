// 包声明
package com.debuff.framework.config;

// 导入所需类
import java.util.Random;
import com.google.code.kaptcha.text.impl.DefaultTextCreator;

/**
 * 验证码文本生成器
 *
 * @author ruoyi
 */
public class KaptchaTextCreator extends DefaultTextCreator // 继承默认文本生成器，实现数学运算验证码
{
    private static final String[] CNUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(","); // 数字中文数组，用于显示运算表达式

    @Override
    public String getText() // 生成数学运算验证码文本
    {
        Integer result = 0; // 运算结果
        Random random = new Random(); // 创建随机数生成器
        int x = random.nextInt(10); // 生成第一个0-9的随机数
        int y = random.nextInt(10); // 生成第二个0-9的随机数
        StringBuilder suChinese = new StringBuilder(); // 用于拼接运算表达式
        int randomoperands = random.nextInt(3); // 随机选择运算类型：0=乘法，1=除法/加法，2=减法
        if (randomoperands == 0) // 乘法运算
        {
            result = x * y; // 计算乘法结果
            suChinese.append(CNUMBERS[x]); // 拼接第一个操作数
            suChinese.append("*"); // 拼接乘号
            suChinese.append(CNUMBERS[y]); // 拼接第二个操作数
        }
        else if (randomoperands == 1) // 除法或加法运算
        {
            if ((x != 0) && y % x == 0) // 如果能整除则用除法
            {
                result = y / x; // 计算除法结果
                suChinese.append(CNUMBERS[y]); // 拼接被除数
                suChinese.append("/"); // 拼接除号
                suChinese.append(CNUMBERS[x]); // 拼接除数
            }
            else // 不能整除则改用加法
            {
                result = x + y; // 计算加法结果
                suChinese.append(CNUMBERS[x]); // 拼接第一个加数
                suChinese.append("+"); // 拼接加号
                suChinese.append(CNUMBERS[y]); // 拼接第二个加数
            }
        }
        else // 减法运算
        {
            if (x >= y) // 确保结果不为负数
            {
                result = x - y; // 计算减法结果
                suChinese.append(CNUMBERS[x]); // 拼接被减数
                suChinese.append("-"); // 拼接减号
                suChinese.append(CNUMBERS[y]); // 拼接减数
            }
            else // x < y时交换顺序
            {
                result = y - x; // 计算减法结果
                suChinese.append(CNUMBERS[y]); // 拼接被减数
                suChinese.append("-"); // 拼接减号
                suChinese.append(CNUMBERS[x]); // 拼接减数
            }
        }
        suChinese.append("=?@" + result); // 拼接"=?@结果"，@后面是正确答案
        return suChinese.toString(); // 返回完整的验证码文本，如"3+5=?@8"
    }
}
