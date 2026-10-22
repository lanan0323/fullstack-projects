// 包声明：任务调度常量所在包
package com.debuff.common.constant;

/**
 * 任务调度通用常量
 * 
 * @author ruoyi
 */
public class ScheduleConstants
{
    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME"; // 任务类名在调度配置中的key

    /** 执行目标key */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES"; // 任务属性在调度配置中的key

    /** 默认 */
    public static final String MISFIRE_DEFAULT = "0"; // 默认错失策略

    /** 立即触发执行 */
    public static final String MISFIRE_IGNORE_MISFIRES = "1"; // 错失后立即触发执行

    /** 触发一次执行 */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2"; // 错失后触发一次并继续

    /** 不触发立即执行 */
    public static final String MISFIRE_DO_NOTHING = "3"; // 错失后不执行，等待下次调度

    // 任务状态枚举
    public enum Status
    {
        /**
         * 正常
         */
        NORMAL("0"), // 正常状态
        /**
         * 暂停
         */
        PAUSE("1"); // 暂停状态

        private String value; // 状态值

        private Status(String value) // 构造方法
        {
            this.value = value; // 赋值状态值
        }

        public String getValue() // 获取状态值
        {
            return value; // 返回状态值
        }
    }
}
