// 包声明
package com.debuff.generator.util;

// 导入集合、Velocity上下文、JSON解析等依赖
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.velocity.VelocityContext;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.debuff.common.constant.GenConstants;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.generator.domain.GenTable;
import com.debuff.generator.domain.GenTableColumn;

/**
 * 模板处理工具类
 * 
 * @author ruoyi
 */
// Velocity模板工具类，负责准备模板上下文数据、获取模板列表、计算文件名等
public class VelocityUtils
{
    /** 项目空间路径 */
    // Java源码路径前缀
    private static final String PROJECT_PATH = "main/java";

    /** mybatis空间路径 */
    // MyBatis Mapper XML路径前缀
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /** 默认上级菜单，系统工具 */
    // 默认父菜单ID(系统工具)
    private static final String DEFAULT_PARENT_MENU_ID = "3";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    // 准备Velocity模板上下文，填充所有模板渲染所需的变量
    public static VelocityContext prepareContext(GenTable genTable)
    {
        String moduleName = genTable.getModuleName(); // 模块名
        String businessName = genTable.getBusinessName(); // 业务名
        String packageName = genTable.getPackageName(); // 包名
        String tplCategory = genTable.getTplCategory(); // 模板类型(crud/tree/sub)
        String functionName = genTable.getFunctionName(); // 功能名称

        VelocityContext velocityContext = new VelocityContext(); // 创建模板上下文
        velocityContext.put("tplCategory", genTable.getTplCategory()); // 模板类型
        velocityContext.put("tableName", genTable.getTableName()); // 表名
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】"); // 功能名(空时给默认提示)
        velocityContext.put("ClassName", genTable.getClassName()); // 首字母大写类名
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName())); // 首字母小写类名
        velocityContext.put("moduleName", genTable.getModuleName()); // 模块名
        velocityContext.put("BusinessName", StringUtils.capitalize(genTable.getBusinessName())); // 首字母大写业务名
        velocityContext.put("businessName", genTable.getBusinessName()); // 业务名
        velocityContext.put("basePackage", getPackagePrefix(packageName)); // 基础包路径(去掉最后一段)
        velocityContext.put("packageName", packageName); // 完整包名
        velocityContext.put("author", genTable.getFunctionAuthor()); // 作者
        velocityContext.put("datetime", DateUtils.getDate()); // 当前日期
        velocityContext.put("pkColumn", genTable.getPkColumn()); // 主键列
        velocityContext.put("importList", getImportList(genTable)); // 需要导入的Java包列表
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName)); // 权限前缀
        velocityContext.put("columns", genTable.getColumns()); // 字段列表
        velocityContext.put("table", genTable); // 表对象本身
        velocityContext.put("dicts", getDicts(genTable)); // 字典类型列表
        setMenuVelocityContext(velocityContext, genTable); // 设置菜单相关变量
        if (GenConstants.TPL_TREE.equals(tplCategory)) // 如果是树形模板
        {
            setTreeVelocityContext(velocityContext, genTable); // 设置树形相关变量
        }
        if (GenConstants.TPL_SUB.equals(tplCategory)) // 如果是主子表模板
        {
            setSubVelocityContext(velocityContext, genTable); // 设置子表相关变量
        }
        return velocityContext; // 返回填充好的上下文
    }

    // 设置菜单相关模板变量(父菜单ID)
    public static void setMenuVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions(); // 获取配置JSON
        JSONObject paramsObj = JSON.parseObject(options); // 解析JSON
        String parentMenuId = getParentMenuId(paramsObj); // 获取父菜单ID
        context.put("parentMenuId", parentMenuId); // 放入上下文
    }

    // 设置树形模板变量(树编码/树父编码/树名称/展开列)
    public static void setTreeVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions(); // 获取配置JSON
        JSONObject paramsObj = JSON.parseObject(options); // 解析JSON
        String treeCode = getTreecode(paramsObj); // 树编码字段(驼峰)
        String treeParentCode = getTreeParentCode(paramsObj); // 树父编码字段(驼峰)
        String treeName = getTreeName(paramsObj); // 树名称字段(驼峰)

        context.put("treeCode", treeCode); // 树编码
        context.put("treeParentCode", treeParentCode); // 树父编码
        context.put("treeName", treeName); // 树名称
        context.put("expandColumn", getExpandColumn(genTable)); // 展开按钮所在列
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE)) // 如果有树父编码配置
        {
            context.put("tree_parent_code", paramsObj.getString(GenConstants.TREE_PARENT_CODE)); // 原始树父编码
        }
        if (paramsObj.containsKey(GenConstants.TREE_NAME)) // 如果有树名称配置
        {
            context.put("tree_name", paramsObj.getString(GenConstants.TREE_NAME)); // 原始树名称
        }
    }

    // 设置主子表模板变量(子表信息/外键/类名等)
    public static void setSubVelocityContext(VelocityContext context, GenTable genTable)
    {
        GenTable subTable = genTable.getSubTable(); // 获取子表对象
        String subTableName = genTable.getSubTableName(); // 子表名
        String subTableFkName = genTable.getSubTableFkName(); // 子表外键名
        String subClassName = genTable.getSubTable().getClassName(); // 子表类名
        String subTableFkClassName = StringUtils.convertToCamelCase(subTableFkName); // 外键字段转驼峰

        context.put("subTable", subTable); // 子表对象
        context.put("subTableName", subTableName); // 子表名
        context.put("subTableFkName", subTableFkName); // 外键列名
        context.put("subTableFkClassName", subTableFkClassName); // 外键驼峰名(首字母大写)
        context.put("subTableFkclassName", StringUtils.uncapitalize(subTableFkClassName)); // 外键驼峰名(首字母小写)
        context.put("subClassName", subClassName); // 子表类名(首字母大写)
        context.put("subclassName", StringUtils.uncapitalize(subClassName)); // 子表类名(首字母小写)
        context.put("subImportList", getImportList(genTable.getSubTable())); // 子表需要导入的包
    }

    /**
     * 获取模板信息
     * @param tplCategory 生成的模板
     * @param tplWebType 前端类型
     * @return 模板列表
     */
    // 根据模板类型和前端类型获取需要渲染的模板文件列表
    public static List<String> getTemplateList(String tplCategory, String tplWebType)
    {
        String useWebType = "vm/vue"; // 默认Vue2模板路径
        if ("element-plus".equals(tplWebType)) // 如果使用Element Plus
        {
            useWebType = "vm/vue/v3"; // 使用Vue3模板路径
        }
        List<String> templates = new ArrayList<String>(); // 模板列表
        templates.add("vm/java/domain.java.vm"); // 实体类模板
        templates.add("vm/java/mapper.java.vm"); // Mapper接口模板
        templates.add("vm/java/service.java.vm"); // Service接口模板
        templates.add("vm/java/serviceImpl.java.vm"); // Service实现类模板
        templates.add("vm/java/controller.java.vm"); // Controller模板
        templates.add("vm/xml/mapper.xml.vm"); // Mapper XML模板
        templates.add("vm/sql/sql.vm"); // SQL菜单脚本模板
        templates.add("vm/js/api.js.vm"); // 前端API接口模板
        if (GenConstants.TPL_CRUD.equals(tplCategory)) // 普通CRUD
        {
            templates.add(useWebType + "/index.vue.vm"); // 普通列表页面
        }
        else if (GenConstants.TPL_TREE.equals(tplCategory)) // 树形
        {
            templates.add(useWebType + "/index-tree.vue.vm"); // 树形列表页面
        }
        else if (GenConstants.TPL_SUB.equals(tplCategory)) // 主子表
        {
            templates.add(useWebType + "/index.vue.vm"); // 主子表列表页面
            templates.add("vm/java/sub-domain.java.vm"); // 子表实体类模板
        }
        return templates; // 返回模板列表
    }

    /**
     * 获取文件名
     */
    // 根据模板路径和表信息计算生成文件的输出路径
    public static String getFileName(String template, GenTable genTable)
    {
        // 文件名称
        String fileName = ""; // 初始化文件名
        // 包路径
        String packageName = genTable.getPackageName(); // 获取包名
        // 模块名
        String moduleName = genTable.getModuleName(); // 获取模块名
        // 大写类名
        String className = genTable.getClassName(); // 获取类名
        // 业务名称
        String businessName = genTable.getBusinessName(); // 获取业务名

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/"); // Java源码完整路径
        String mybatisPath = MYBATIS_PATH + "/" + moduleName; // MyBatis XML路径
        String vuePath = "vue"; // 前端文件路径

        if (template.contains("domain.java.vm")) // 实体类模板
        {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, className); // 生成实体类路径
        }
        if (template.contains("sub-domain.java.vm") && StringUtils.equals(GenConstants.TPL_SUB, genTable.getTplCategory())) // 子表实体类
        {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, genTable.getSubTable().getClassName()); // 生成子表实体类路径
        }
        else if (template.contains("mapper.java.vm")) // Mapper接口模板
        {
            fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, className); // 生成Mapper接口路径
        }
        else if (template.contains("service.java.vm")) // Service接口模板
        {
            fileName = StringUtils.format("{}/service/I{}Service.java", javaPath, className); // 生成Service接口路径
        }
        else if (template.contains("serviceImpl.java.vm")) // Service实现类模板
        {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className); // 生成Service实现类路径
        }
        else if (template.contains("controller.java.vm")) // Controller模板
        {
            fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className); // 生成Controller路径
        }
        else if (template.contains("mapper.xml.vm")) // Mapper XML模板
        {
            fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, className); // 生成Mapper XML路径
        }
        else if (template.contains("sql.vm")) // SQL脚本模板
        {
            fileName = businessName + "Menu.sql"; // 生成菜单SQL文件名
        }
        else if (template.contains("api.js.vm")) // API接口模板
        {
            fileName = StringUtils.format("{}/api/{}/{}.js", vuePath, moduleName, businessName); // 生成前端API路径
        }
        else if (template.contains("index.vue.vm")) // Vue页面模板
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName); // 生成Vue页面路径
        }
        else if (template.contains("index-tree.vue.vm")) // Vue树形页面模板
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName); // 生成Vue树形页面路径
        }
        return fileName; // 返回生成文件路径
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    // 获取包名的前缀部分(去掉最后一段)
    public static String getPackagePrefix(String packageName)
    {
        int lastIndex = packageName.lastIndexOf("."); // 找最后一个点
        return StringUtils.substring(packageName, 0, lastIndex); // 截取点之前的部分
    }

    /**
     * 根据列类型获取导入包
     * 
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    // 根据字段Java类型收集需要import的包(如Date/BigDecimal)
    public static HashSet<String> getImportList(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns(); // 获取字段列表
        GenTable subGenTable = genTable.getSubTable(); // 获取子表
        HashSet<String> importList = new HashSet<String>(); // 用Set去重
        if (StringUtils.isNotNull(subGenTable)) // 如果有子表
        {
            importList.add("java.util.List"); // 子表需要List
        }
        for (GenTableColumn column : columns) // 遍历字段
        {
            if (!column.isSuperColumn() && GenConstants.TYPE_DATE.equals(column.getJavaType())) // Date类型字段
            {
                importList.add("java.util.Date"); // 导入Date
                importList.add("com.fasterxml.jackson.annotation.JsonFormat"); // 导入日期格式化注解
            }
            else if (!column.isSuperColumn() && GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType())) // BigDecimal类型
            {
                importList.add("java.math.BigDecimal"); // 导入BigDecimal
            }
        }
        return importList; // 返回需要导入的包集合
    }

    /**
     * 根据列类型获取字典组
     * 
     * @param genTable 业务表对象
     * @return 返回字典组
     */
    // 收集所有字段中使用的字典类型(用于前端加载字典数据)
    public static String getDicts(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns(); // 主表字段
        Set<String> dicts = new HashSet<String>(); // 字典类型集合(去重)
        addDicts(dicts, columns); // 添加主表字段的字典类型
        if (StringUtils.isNotNull(genTable.getSubTable())) // 如果有子表
        {
            List<GenTableColumn> subColumns = genTable.getSubTable().getColumns(); // 子表字段
            addDicts(dicts, subColumns); // 添加子表字段的字典类型
        }
        return StringUtils.join(dicts, ", "); // 拼接为逗号分隔字符串
    }

    /**
     * 添加字典列表
     * 
     * @param dicts 字典列表
     * @param columns 列集合
     */
    // 将字段中使用了下拉框/单选框/复选框的字典类型添加到集合
    public static void addDicts(Set<String> dicts, List<GenTableColumn> columns)
    {
        for (GenTableColumn column : columns) // 遍历字段
        {
            // 非继承字段且有字典类型且控件类型是选择类
            if (!column.isSuperColumn() && StringUtils.isNotEmpty(column.getDictType()) && StringUtils.equalsAny(
                    column.getHtmlType(),
                    new String[] { GenConstants.HTML_SELECT, GenConstants.HTML_RADIO, GenConstants.HTML_CHECKBOX }))
            {
                dicts.add("'" + column.getDictType() + "'"); // 加引号后加入集合
            }
        }
    }

    /**
     * 获取权限前缀
     *
     * @param moduleName 模块名称
     * @param businessName 业务名称
     * @return 返回权限前缀
     */
    // 生成权限标识前缀，格式为"模块名:业务名"
    public static String getPermissionPrefix(String moduleName, String businessName)
    {
        return StringUtils.format("{}:{}", moduleName, businessName);
    }

    /**
     * 获取上级菜单ID字段
     *
     * @param paramsObj 生成其他选项
     * @return 上级菜单ID字段
     */
    // 从配置JSON中获取父菜单ID，没有则返回默认值
    public static String getParentMenuId(JSONObject paramsObj)
    {
        if (StringUtils.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.PARENT_MENU_ID)
                && StringUtils.isNotEmpty(paramsObj.getString(GenConstants.PARENT_MENU_ID)))
        {
            return paramsObj.getString(GenConstants.PARENT_MENU_ID); // 返回配置的父菜单ID
        }
        return DEFAULT_PARENT_MENU_ID; // 返回默认父菜单ID(系统工具)
    }

    /**
     * 获取树编码
     *
     * @param paramsObj 生成其他选项
     * @return 树编码
     */
    // 从配置JSON中获取树编码字段名(转驼峰)
    public static String getTreecode(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_CODE)) // 如果有树编码配置
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_CODE)); // 转驼峰返回
        }
        return StringUtils.EMPTY; // 没有配置返回空
    }

    /**
     * 获取树父编码
     *
     * @param paramsObj 生成其他选项
     * @return 树父编码
     */
    // 从配置JSON中获取树父编码字段名(转驼峰)
    public static String getTreeParentCode(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE)) // 如果有树父编码配置
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_PARENT_CODE)); // 转驼峰返回
        }
        return StringUtils.EMPTY; // 没有配置返回空
    }

    /**
     * 获取树名称
     *
     * @param paramsObj 生成其他选项
     * @return 树名称
     */
    // 从配置JSON中获取树名称字段名(转驼峰)
    public static String getTreeName(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_NAME)) // 如果有树名称配置
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_NAME)); // 转驼峰返回
        }
        return StringUtils.EMPTY; // 没有配置返回空
    }

    /**
     * 获取需要在哪一列上面显示展开按钮
     *
     * @param genTable 业务表对象
     * @return 展开按钮列序号
     */
    // 计算树形表格展开按钮应显示在第几列(树名称字段所在的列表序号)
    public static int getExpandColumn(GenTable genTable)
    {
        String options = genTable.getOptions(); // 获取配置JSON
        JSONObject paramsObj = JSON.parseObject(options); // 解析JSON
        String treeName = paramsObj.getString(GenConstants.TREE_NAME); // 获取树名称字段
        int num = 0; // 列计数器
        for (GenTableColumn column : genTable.getColumns()) // 遍历字段
        {
            if (column.isList()) // 只计算在列表中显示的字段
            {
                num++; // 列号递增
                String columnName = column.getColumnName(); // 获取列名
                if (columnName.equals(treeName)) // 找到树名称对应的列
                {
                    break; // 停止计数
                }
            }
        }
        return num; // 返回展开按钮所在列号
    }
}
