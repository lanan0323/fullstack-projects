// 包声明：SQL操作工具类所在包
package com.debuff.common.utils.sql;

// 导入异常和字符串工具
import com.debuff.common.exception.UtilException;
import com.debuff.common.utils.StringUtils;

/**
 * sql操作工具类
 * 
 * @author ruoyi
 */
public class SqlUtil // SQL操作工具类，防止SQL注入
{
    /**
     * 定义常用的 sql关键字
     */
    public static String SQL_REGEX = "and |extractvalue|updatexml|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |+|user()"; // SQL危险关键字正则

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+"; // 合法SQL字符正则

    /**
     * 限制orderBy最大长度
     */
    private static final int ORDER_BY_MAX_LENGTH = 500; // orderBy参数最大长度限制

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) // 校验orderBy参数安全性
    {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) // 不为空且不合法时
        {
            throw new UtilException("参数不符合规范，不能进行查询"); // 抛出异常
        }
        if (StringUtils.length(value) > ORDER_BY_MAX_LENGTH) // 超过最大长度时
        {
            throw new UtilException("参数已超过最大限制，不能进行查询"); // 抛出异常
        }
        return value; // 校验通过返回原值
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value) // 验证orderBy值是否只包含合法字符
    {
        return value.matches(SQL_PATTERN); // 用正则匹配
    }

    /**
     * SQL关键字检查
     */
    public static void filterKeyword(String value) // 检查参数中是否包含SQL注入关键字
    {
        if (StringUtils.isEmpty(value)) // 值为空时直接返回
        {
            return;
        }
        String[] sqlKeywords = StringUtils.split(SQL_REGEX, "\\|"); // 按|分割关键字
        for (String sqlKeyword : sqlKeywords) // 遍历所有关键字
        {
            if (StringUtils.indexOfIgnoreCase(value, sqlKeyword) > -1) // 包含关键字时（忽略大小写）
            {
                throw new UtilException("参数存在SQL注入风险"); // 抛出异常
            }
        }
    }
}
