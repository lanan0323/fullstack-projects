// 包声明
package com.debuff.generator.util;

// 导入数组工具和配置/实体类
import java.util.Arrays;
import org.apache.commons.lang3.RegExUtils;
import com.debuff.common.constant.GenConstants;
import com.debuff.common.utils.StringUtils;
import com.debuff.generator.config.GenConfig;
import com.debuff.generator.domain.GenTable;
import com.debuff.generator.domain.GenTableColumn;

/**
 * 代码生成器 工具类
 * 
 * @author ruoyi
 */
// 代码生成工具类，提供表/字段初始化、名称转换等功能
public class GenUtils
{
    /**
     * 初始化表信息
     */
    // 初始化表对象的基本属性(类名/包名/模块名/业务名/功能名/作者)
    public static void initTable(GenTable genTable, String operName)
    {
        genTable.setClassName(convertClassName(genTable.getTableName())); // 表名转Java类名
        genTable.setPackageName(GenConfig.getPackageName()); // 设置包路径
        genTable.setModuleName(getModuleName(GenConfig.getPackageName())); // 从包路径提取模块名
        genTable.setBusinessName(getBusinessName(genTable.getTableName())); // 从表名提取业务名
        genTable.setFunctionName(replaceText(genTable.getTableComment())); // 处理表注释作为功能名
        genTable.setFunctionAuthor(GenConfig.getAuthor()); // 设置作者
        genTable.setCreateBy(operName); // 设置创建人
    }

    /**
     * 初始化列属性字段
     */
    // 根据数据库列类型初始化字段的Java类型/HTML控件类型/查询方式等
    public static void initColumnField(GenTableColumn column, GenTable table)
    {
        String dataType = getDbType(column.getColumnType()); // 获取数据库字段类型(去掉长度)
        String columnName = column.getColumnName(); // 获取列名
        column.setTableId(table.getTableId()); // 设置所属表ID
        column.setCreateBy(table.getCreateBy()); // 设置创建人
        // 设置java字段名
        column.setJavaField(StringUtils.toCamelCase(columnName)); // 列名转驼峰命名
        // 设置默认类型
        column.setJavaType(GenConstants.TYPE_STRING); // 默认Java类型为String
        column.setQueryType(GenConstants.QUERY_EQ); // 默认查询方式为等于

        if (arraysContains(GenConstants.COLUMNTYPE_STR, dataType) || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType))
        {
            // 字符串长度超过500设置为文本域
            Integer columnLength = getColumnLength(column.getColumnType()); // 获取字段长度
            String htmlType = columnLength >= 500 || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType) ? GenConstants.HTML_TEXTAREA : GenConstants.HTML_INPUT; // 长文本用textarea，短文本用input
            column.setHtmlType(htmlType); // 设置HTML控件类型
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_TIME, dataType))
        {
            column.setJavaType(GenConstants.TYPE_DATE); // 时间类型设为Date
            column.setHtmlType(GenConstants.HTML_DATETIME); // 控件设为日期时间
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_NUMBER, dataType))
        {
            column.setHtmlType(GenConstants.HTML_INPUT); // 数字类型用input控件

            // 如果是浮点型 统一用BigDecimal
            String[] str = StringUtils.split(StringUtils.substringBetween(column.getColumnType(), "(", ")"), ","); // 解析精度
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) // 有小数位
            {
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL); // 浮点型用BigDecimal
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) // 长度<=10的整数
            {
                column.setJavaType(GenConstants.TYPE_INTEGER); // 用Integer
            }
            // 长整形
            else
            {
                column.setJavaType(GenConstants.TYPE_LONG); // 其他用Long
            }
        }

        // 插入字段（默认所有字段都需要插入）
        column.setIsInsert(GenConstants.REQUIRE);

        // 编辑字段：排除特定字段和主键
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_EDIT, columnName) && !column.isPk())
        {
            column.setIsEdit(GenConstants.REQUIRE); // 允许编辑
        }
        // 列表字段：排除特定字段和主键
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_LIST, columnName) && !column.isPk())
        {
            column.setIsList(GenConstants.REQUIRE); // 在列表中显示
        }
        // 查询字段：排除特定字段和主键
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_QUERY, columnName) && !column.isPk())
        {
            column.setIsQuery(GenConstants.REQUIRE); // 允许查询
        }

        // 查询字段类型：name结尾的字段默认模糊查询
        if (StringUtils.endsWithIgnoreCase(columnName, "name"))
        {
            column.setQueryType(GenConstants.QUERY_LIKE); // 设置为LIKE查询
        }
        // 状态字段设置单选框
        if (StringUtils.endsWithIgnoreCase(columnName, "status"))
        {
            column.setHtmlType(GenConstants.HTML_RADIO); // 状态用单选框
        }
        // 类型&性别字段设置下拉框
        else if (StringUtils.endsWithIgnoreCase(columnName, "type")
                || StringUtils.endsWithIgnoreCase(columnName, "sex"))
        {
            column.setHtmlType(GenConstants.HTML_SELECT); // 类型/性别用下拉框
        }
        // 图片字段设置图片上传控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "image"))
        {
            column.setHtmlType(GenConstants.HTML_IMAGE_UPLOAD); // 图片用图片上传
        }
        // 文件字段设置文件上传控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "file"))
        {
            column.setHtmlType(GenConstants.HTML_FILE_UPLOAD); // 文件用文件上传
        }
        // 内容字段设置富文本控件
        else if (StringUtils.endsWithIgnoreCase(columnName, "content"))
        {
            column.setHtmlType(GenConstants.HTML_EDITOR); // 内容用富文本编辑器
        }
    }

    /**
     * 校验数组是否包含指定值
     * 
     * @param arr 数组
     * @param targetValue 值
     * @return 是否包含
     */
    // 判断数组是否包含指定值
    public static boolean arraysContains(String[] arr, String targetValue)
    {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取模块名
     * 
     * @param packageName 包名
     * @return 模块名
     */
    // 从包路径中提取最后一段作为模块名
    public static String getModuleName(String packageName)
    {
        int lastIndex = packageName.lastIndexOf("."); // 找最后一个点
        int nameLength = packageName.length(); // 包名长度
        return StringUtils.substring(packageName, lastIndex + 1, nameLength); // 截取最后一段
    }

    /**
     * 获取业务名
     * 
     * @param tableName 表名
     * @return 业务名
     */
    // 从表名中提取最后一段下划线后的内容作为业务名
    public static String getBusinessName(String tableName)
    {
        int lastIndex = tableName.lastIndexOf("_"); // 找最后一个下划线
        int nameLength = tableName.length(); // 表名长度
        return StringUtils.substring(tableName, lastIndex + 1, nameLength); // 截取最后一段
    }

    /**
     * 表名转换成Java类名
     * 
     * @param tableName 表名称
     * @return 类名
     */
    // 表名转Java类名(可选去除前缀后转驼峰)
    public static String convertClassName(String tableName)
    {
        boolean autoRemovePre = GenConfig.getAutoRemovePre(); // 是否自动去除前缀
        String tablePrefix = GenConfig.getTablePrefix(); // 获取配置的前缀
        if (autoRemovePre && StringUtils.isNotEmpty(tablePrefix)) // 如果启用去前缀
        {
            String[] searchList = StringUtils.split(tablePrefix, ","); // 支持多个前缀
            tableName = replaceFirst(tableName, searchList); // 去除第一个匹配的前缀
        }
        return StringUtils.convertToCamelCase(tableName); // 转驼峰命名
    }

    /**
     * 批量替换前缀
     * 
     * @param replacementm 替换值
     * @param searchList 替换列表
     * @return
     */
    // 如果字符串以指定前缀开头则去除第一个匹配的前缀
    public static String replaceFirst(String replacementm, String[] searchList)
    {
        String text = replacementm; // 原始文本
        for (String searchString : searchList) // 遍历前缀列表
        {
            if (replacementm.startsWith(searchString)) // 如果匹配
            {
                text = replacementm.replaceFirst(searchString, ""); // 去除前缀
                break; // 只替换第一个
            }
        }
        return text; // 返回处理后的文本
    }

    /**
     * 关键字替换
     * 
     * @param text 需要被替换的名字
     * @return 替换后的名字
     */
    // 去除文本中的"表"和"若依"关键字
    public static String replaceText(String text)
    {
        return RegExUtils.replaceAll(text, "(?:表|若依)", "");
    }

    /**
     * 获取数据库类型字段
     * 
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    // 获取数据库字段类型(去掉括号中的长度定义)
    public static String getDbType(String columnType)
    {
        if (StringUtils.indexOf(columnType, "(") > 0) // 如果包含括号
        {
            return StringUtils.substringBefore(columnType, "("); // 截取括号前的类型名
        }
        else
        {
            return columnType; // 没有括号直接返回
        }
    }

    /**
     * 获取字段长度
     * 
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    // 获取字段定义中的长度值
    public static Integer getColumnLength(String columnType)
    {
        if (StringUtils.indexOf(columnType, "(") > 0) // 如果包含括号
        {
            String length = StringUtils.substringBetween(columnType, "(", ")"); // 提取括号中的长度
            return Integer.valueOf(length); // 转为整数
        }
        else
        {
            return 0; // 没有长度定义返回0
        }
    }
}
