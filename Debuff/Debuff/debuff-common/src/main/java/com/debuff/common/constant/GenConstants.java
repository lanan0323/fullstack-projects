// 包声明：代码生成常量所在包
package com.debuff.common.constant;

/**
 * 代码生成通用常量
 * 
 * @author ruoyi
 */
public class GenConstants
{
    /** 单表（增删改查） */
    public static final String TPL_CRUD = "crud"; // 单表CRUD模板标识

    /** 树表（增删改查） */
    public static final String TPL_TREE = "tree"; // 树表CRUD模板标识

    /** 主子表（增删改查） */
    public static final String TPL_SUB = "sub"; // 主子表CRUD模板标识

    /** 树编码字段 */
    public static final String TREE_CODE = "treeCode"; // 树节点编码字段名

    /** 树父编码字段 */
    public static final String TREE_PARENT_CODE = "treeParentCode"; // 树父节点编码字段名

    /** 树名称字段 */
    public static final String TREE_NAME = "treeName"; // 树节点名称字段名

    /** 上级菜单ID字段 */
    public static final String PARENT_MENU_ID = "parentMenuId"; // 上级菜单ID字段名

    /** 上级菜单名称字段 */
    public static final String PARENT_MENU_NAME = "parentMenuName"; // 上级菜单名称字段名

    /** 数据库字符串类型 */
    public static final String[] COLUMNTYPE_STR = { "char", "varchar", "nvarchar", "varchar2" }; // 数据库字符串类型列表

    /** 数据库文本类型 */
    public static final String[] COLUMNTYPE_TEXT = { "tinytext", "text", "mediumtext", "longtext" }; // 数据库文本类型列表

    /** 数据库时间类型 */
    public static final String[] COLUMNTYPE_TIME = { "datetime", "time", "date", "timestamp" }; // 数据库时间类型列表

    /** 数据库数字类型 */
    public static final String[] COLUMNTYPE_NUMBER = { "tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bit", "bigint", "float", "double", "decimal" }; // 数据库数字类型列表

    /** 页面不需要编辑字段 */
    public static final String[] COLUMNNAME_NOT_EDIT = { "id", "create_by", "create_time", "del_flag" }; // 页面上不可编辑的字段

    /** 页面不需要显示的列表字段 */
    public static final String[] COLUMNNAME_NOT_LIST = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time" }; // 列表页不需要显示的字段

    /** 页面不需要查询字段 */
    public static final String[] COLUMNNAME_NOT_QUERY = { "id", "create_by", "create_time", "del_flag", "update_by",
            "update_time", "remark" }; // 查询条件中不需要出现的字段

    /** Entity基类字段 */
    public static final String[] BASE_ENTITY = { "createBy", "createTime", "updateBy", "updateTime", "remark" }; // 实体基类包含的字段

    /** Tree基类字段 */
    public static final String[] TREE_ENTITY = { "parentName", "parentId", "orderNum", "ancestors", "children" }; // 树形实体基类包含的字段

    /** 文本框 */
    public static final String HTML_INPUT = "input"; // HTML文本输入框组件

    /** 文本域 */
    public static final String HTML_TEXTAREA = "textarea"; // HTML多行文本域组件

    /** 下拉框 */
    public static final String HTML_SELECT = "select"; // HTML下拉选择框组件

    /** 单选框 */
    public static final String HTML_RADIO = "radio"; // HTML单选按钮组件

    /** 复选框 */
    public static final String HTML_CHECKBOX = "checkbox"; // HTML复选框组件

    /** 日期控件 */
    public static final String HTML_DATETIME = "datetime"; // HTML日期时间选择控件

    /** 图片上传控件 */
    public static final String HTML_IMAGE_UPLOAD = "imageUpload"; // 图片上传组件

    /** 文件上传控件 */
    public static final String HTML_FILE_UPLOAD = "fileUpload"; // 文件上传组件

    /** 富文本控件 */
    public static final String HTML_EDITOR = "editor"; // 富文本编辑器组件

    /** 字符串类型 */
    public static final String TYPE_STRING = "String"; // Java字符串类型

    /** 整型 */
    public static final String TYPE_INTEGER = "Integer"; // Java整型类型

    /** 长整型 */
    public static final String TYPE_LONG = "Long"; // Java长整型类型

    /** 浮点型 */
    public static final String TYPE_DOUBLE = "Double"; // Java浮点型类型

    /** 高精度计算类型 */
    public static final String TYPE_BIGDECIMAL = "BigDecimal"; // Java高精度数值类型

    /** 时间类型 */
    public static final String TYPE_DATE = "Date"; // Java日期类型

    /** 模糊查询 */
    public static final String QUERY_LIKE = "LIKE"; // SQL模糊查询方式

    /** 相等查询 */
    public static final String QUERY_EQ = "EQ"; // SQL精确匹配查询方式

    /** 需要 */
    public static final String REQUIRE = "1"; // 需要标识
}
