// 包声明
package com.debuff.generator.service;

// 导入IO流、集合、Velocity模板引擎、事务等依赖
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.debuff.common.constant.Constants;
import com.debuff.common.constant.GenConstants;
import com.debuff.common.core.text.CharsetKit;
import com.debuff.common.exception.ServiceException;
import com.debuff.common.utils.SecurityUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.generator.domain.GenTable;
import com.debuff.generator.domain.GenTableColumn;
import com.debuff.generator.mapper.GenTableColumnMapper;
import com.debuff.generator.mapper.GenTableMapper;
import com.debuff.generator.util.GenUtils;
import com.debuff.generator.util.VelocityInitializer;
import com.debuff.generator.util.VelocityUtils;

/**
 * 业务 服务层实现
 * 
 * @author ruoyi
 */
// 注册为Spring服务组件
@Service
// 代码生成表服务实现类
public class GenTableServiceImpl implements IGenTableService
{
    // 日志对象
    private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

    // 注入代码生成表Mapper
    @Autowired
    private GenTableMapper genTableMapper;

    // 注入代码生成字段Mapper
    @Autowired
    private GenTableColumnMapper genTableColumnMapper;

    /**
     * 查询业务信息
     * 
     * @param id 业务ID
     * @return 业务信息
     */
    @Override
    // 根据ID查询表信息并解析配置选项
    public GenTable selectGenTableById(Long id)
    {
        GenTable genTable = genTableMapper.selectGenTableById(id); // 查询表信息
        setTableFromOptions(genTable); // 从options JSON中提取树/菜单等配置
        return genTable;
    }

    /**
     * 查询业务列表
     * 
     * @param genTable 业务信息
     * @return 业务集合
     */
    @Override
    // 查询已导入的代码生成表列表
    public List<GenTable> selectGenTableList(GenTable genTable)
    {
        return genTableMapper.selectGenTableList(genTable);
    }

    /**
     * 查询据库列表
     * 
     * @param genTable 业务信息
     * @return 数据库表集合
     */
    @Override
    // 查询数据库中用户表(未导入的)
    public List<GenTable> selectDbTableList(GenTable genTable)
    {
        return genTableMapper.selectDbTableList(genTable);
    }

    /**
     * 查询据库列表
     * 
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    @Override
    // 根据表名数组查询数据库表
    public List<GenTable> selectDbTableListByNames(String[] tableNames)
    {
        return genTableMapper.selectDbTableListByNames(tableNames);
    }

    /**
     * 查询所有表信息
     * 
     * @return 表信息集合
     */
    @Override
    // 查询所有已导入的代码生成表
    public List<GenTable> selectGenTableAll()
    {
        return genTableMapper.selectGenTableAll();
    }

    /**
     * 修改业务
     * 
     * @param genTable 业务信息
     * @return 结果
     */
    @Override
    // 开启事务
    @Transactional
    // 更新表和字段配置
    public void updateGenTable(GenTable genTable)
    {
        String options = JSON.toJSONString(genTable.getParams()); // 将参数Map转为JSON字符串
        genTable.setOptions(options); // 设置options字段
        int row = genTableMapper.updateGenTable(genTable); // 更新表信息
        if (row > 0)
        {
            for (GenTableColumn cenTableColumn : genTable.getColumns()) // 遍历所有字段
            {
                genTableColumnMapper.updateGenTableColumn(cenTableColumn); // 逐个更新字段
            }
        }
    }

    /**
     * 删除业务对象
     * 
     * @param tableIds 需要删除的数据ID
     * @return 结果
     */
    @Override
    // 开启事务
    @Transactional
    // 删除表及关联字段
    public void deleteGenTableByIds(Long[] tableIds)
    {
        genTableMapper.deleteGenTableByIds(tableIds); // 删除表记录
        genTableColumnMapper.deleteGenTableColumnByIds(tableIds); // 删除关联字段记录
    }

    /**
     * 导入表结构
     * 
     * @param tableList 导入表列表
     */
    @Override
    // 开启事务
    @Transactional
    // 导入数据库表结构到代码生成系统
    public void importGenTable(List<GenTable> tableList)
    {
        String operName = SecurityUtils.getUsername(); // 获取当前登录用户名
        try
        {
            for (GenTable table : tableList) // 遍历每个待导入的表
            {
                String tableName = table.getTableName(); // 获取表名
                GenUtils.initTable(table, operName); // 初始化表信息(类名/包名/模块名等)
                int row = genTableMapper.insertGenTable(table); // 插入表记录
                if (row > 0)
                {
                    // 保存列信息
                    List<GenTableColumn> genTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName); // 查询数据库列信息
                    for (GenTableColumn column : genTableColumns) // 遍历每个列
                    {
                        GenUtils.initColumnField(column, table); // 初始化字段属性(Java类型/HTML类型等)
                        genTableColumnMapper.insertGenTableColumn(column); // 插入字段记录
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("导入失败：" + e.getMessage()); // 导入异常时抛出错误
        }
    }

    /**
     * 预览代码
     * 
     * @param tableId 表编号
     * @return 预览数据列表
     */
    @Override
    // 预览生成的代码(返回文件名-代码内容映射)
    public Map<String, String> previewCode(Long tableId)
    {
        Map<String, String> dataMap = new LinkedHashMap<>(); // 有序Map存储预览结果
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableById(tableId);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);
        VelocityInitializer.initVelocity(); // 初始化Velocity模板引擎

        VelocityContext context = VelocityUtils.prepareContext(table); // 准备模板上下文数据

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory(), table.getTplWebType());
        for (String template : templates) // 遍历每个模板
        {
            // 渲染模板
            StringWriter sw = new StringWriter(); // 字符串写入器
            Template tpl = Velocity.getTemplate(template, Constants.UTF8); // 加载模板文件
            tpl.merge(context, sw); // 合并模板与数据
            dataMap.put(template, sw.toString()); // 存储渲染结果
        }
        return dataMap; // 返回预览数据
    }

    /**
     * 生成代码（下载方式）
     * 
     * @param tableName 表名称
     * @return 数据
     */
    @Override
    // 生成代码并打包为ZIP字节流供下载
    public byte[] downloadCode(String tableName)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // 字节输出流
        ZipOutputStream zip = new ZipOutputStream(outputStream); // ZIP压缩流
        generatorCode(tableName, zip); // 生成代码写入ZIP
        IOUtils.closeQuietly(zip); // 关闭流
        return outputStream.toByteArray(); // 返回ZIP字节数组
    }

    /**
     * 生成代码（自定义路径）
     * 
     * @param tableName 表名称
     */
    @Override
    // 生成代码并写入到自定义路径(非下载)
    public void generatorCode(String tableName)
    {
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);

        VelocityInitializer.initVelocity(); // 初始化Velocity引擎

        VelocityContext context = VelocityUtils.prepareContext(table); // 准备模板上下文

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory(), table.getTplWebType());
        for (String template : templates) // 遍历模板
        {
            // 排除前端模板和SQL模板(这些只在下载时生成)
            if (!StringUtils.containsAny(template, "sql.vm", "api.js.vm", "index.vue.vm", "index-tree.vue.vm"))
            {
                // 渲染模板
                StringWriter sw = new StringWriter(); // 字符串写入器
                Template tpl = Velocity.getTemplate(template, Constants.UTF8); // 加载模板
                tpl.merge(context, sw); // 合并数据
                try
                {
                    String path = getGenPath(table, template); // 计算生成文件路径
                    FileUtils.writeStringToFile(new File(path), sw.toString(), CharsetKit.UTF_8); // 写入文件
                }
                catch (IOException e)
                {
                    throw new ServiceException("渲染模板失败，表名：" + table.getTableName()); // 写入失败抛异常
                }
            }
        }
    }

    /**
     * 同步数据库
     * 
     * @param tableName 表名称
     */
    @Override
    // 开启事务
    @Transactional
    // 同步数据库表结构与代码生成表结构
    public void synchDb(String tableName)
    {
        GenTable table = genTableMapper.selectGenTableByName(tableName); // 查询已导入的表信息
        List<GenTableColumn> tableColumns = table.getColumns(); // 已保存的字段列表
        Map<String, GenTableColumn> tableColumnMap = tableColumns.stream().collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity())); // 按列名建Map

        List<GenTableColumn> dbTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName); // 查询数据库最新列信息
        if (StringUtils.isEmpty(dbTableColumns))
        {
            throw new ServiceException("同步数据失败，原表结构不存在"); // 表不存在时抛异常
        }
        List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumn::getColumnName).collect(Collectors.toList()); // 数据库列名列表

        dbTableColumns.forEach(column -> { // 遍历数据库列
            GenUtils.initColumnField(column, table); // 初始化字段属性
            if (tableColumnMap.containsKey(column.getColumnName())) // 如果字段已存在
            {
                GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName()); // 获取原字段
                column.setColumnId(prevColumn.getColumnId()); // 保留原字段ID
                if (column.isList())
                {
                    // 如果是列表，继续保留查询方式/字典类型选项
                    column.setDictType(prevColumn.getDictType()); // 保留字典类型
                    column.setQueryType(prevColumn.getQueryType()); // 保留查询方式
                }
                if (StringUtils.isNotEmpty(prevColumn.getIsRequired()) && !column.isPk()
                        && (column.isInsert() || column.isEdit())
                        && ((column.isUsableColumn()) || (!column.isSuperColumn())))
                {
                    // 如果是(新增/修改&非主键/非忽略及父属性)，继续保留必填/显示类型选项
                    column.setIsRequired(prevColumn.getIsRequired()); // 保留必填设置
                    column.setHtmlType(prevColumn.getHtmlType()); // 保留控件类型
                }
                genTableColumnMapper.updateGenTableColumn(column); // 更新字段
            }
            else
            {
                genTableColumnMapper.insertGenTableColumn(column); // 新增字段(数据库中新增的列)
            }
        });

        // 找出已删除的字段(数据库中没有但代码生成表中有的)
        List<GenTableColumn> delColumns = tableColumns.stream().filter(column -> !dbTableColumnNames.contains(column.getColumnName())).collect(Collectors.toList());
        if (StringUtils.isNotEmpty(delColumns))
        {
            genTableColumnMapper.deleteGenTableColumns(delColumns); // 删除已不存在的字段
        }
    }

    /**
     * 批量生成代码（下载方式）
     * 
     * @param tableNames 表数组
     * @return 数据
     */
    @Override
    // 批量生成多表代码并打包为ZIP
    public byte[] downloadCode(String[] tableNames)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // 字节输出流
        ZipOutputStream zip = new ZipOutputStream(outputStream); // ZIP压缩流
        for (String tableName : tableNames) // 遍历每个表
        {
            generatorCode(tableName, zip); // 生成代码写入ZIP
        }
        IOUtils.closeQuietly(zip); // 关闭流
        return outputStream.toByteArray(); // 返回ZIP字节数组
    }

    /**
     * 查询表信息并生成代码(写入ZIP)
     */
    // 单表代码生成并写入ZIP流
    private void generatorCode(String tableName, ZipOutputStream zip)
    {
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);

        VelocityInitializer.initVelocity(); // 初始化Velocity引擎

        VelocityContext context = VelocityUtils.prepareContext(table); // 准备模板上下文

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory(), table.getTplWebType());
        for (String template : templates) // 遍历模板
        {
            // 渲染模板
            StringWriter sw = new StringWriter(); // 字符串写入器
            Template tpl = Velocity.getTemplate(template, Constants.UTF8); // 加载模板
            tpl.merge(context, sw); // 合并数据
            try
            {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table))); // 创建ZIP条目
                IOUtils.write(sw.toString(), zip, Constants.UTF8); // 写入渲染后的代码
                IOUtils.closeQuietly(sw); // 关闭写入器
                zip.flush(); // 刷新缓冲区
                zip.closeEntry(); // 关闭当前条目
            }
            catch (IOException e)
            {
                log.error("渲染模板失败，表名：" + table.getTableName(), e); // 记录错误日志
            }
        }
    }

    /**
     * 修改保存参数校验
     * 
     * @param genTable 业务信息
     */
    @Override
    // 校验编辑时的参数合法性
    public void validateEdit(GenTable genTable)
    {
        if (GenConstants.TPL_TREE.equals(genTable.getTplCategory())) // 树形模板
        {
            String options = JSON.toJSONString(genTable.getParams()); // 参数转JSON
            JSONObject paramsObj = JSON.parseObject(options); // 解析JSON对象
            if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_CODE))) // 树编码不能为空
            {
                throw new ServiceException("树编码字段不能为空");
            }
            else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_PARENT_CODE))) // 树父编码不能为空
            {
                throw new ServiceException("树父编码字段不能为空");
            }
            else if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_NAME))) // 树名称不能为空
            {
                throw new ServiceException("树名称字段不能为空");
            }
            else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory())) // 主子表模板
            {
                if (StringUtils.isEmpty(genTable.getSubTableName())) // 子表名不能为空
                {
                    throw new ServiceException("关联子表的表名不能为空");
                }
                else if (StringUtils.isEmpty(genTable.getSubTableFkName())) // 外键名不能为空
                {
                    throw new ServiceException("子表关联的外键名不能为空");
                }
            }
        }
    }

    /**
     * 设置主键列信息
     * 
     * @param table 业务表信息
     */
    // 从字段列表中找到主键列并设置到表对象
    public void setPkColumn(GenTable table)
    {
        for (GenTableColumn column : table.getColumns()) // 遍历字段
        {
            if (column.isPk()) // 找到主键字段
            {
                table.setPkColumn(column); // 设置主键列
                break;
            }
        }
        if (StringUtils.isNull(table.getPkColumn())) // 如果没有主键
        {
            table.setPkColumn(table.getColumns().get(0)); // 默认第一个字段为主键
        }
        if (GenConstants.TPL_SUB.equals(table.getTplCategory())) // 如果是主子表模板
        {
            for (GenTableColumn column : table.getSubTable().getColumns()) // 遍历子表字段
            {
                if (column.isPk()) // 找到子表主键
                {
                    table.getSubTable().setPkColumn(column); // 设置子表主键列
                    break;
                }
            }
            if (StringUtils.isNull(table.getSubTable().getPkColumn())) // 如果子表没有主键
            {
                table.getSubTable().setPkColumn(table.getSubTable().getColumns().get(0)); // 默认第一个字段
            }
        }
    }

    /**
     * 设置主子表信息
     * 
     * @param table 业务表信息
     */
    // 根据子表名查询并设置子表信息
    public void setSubTable(GenTable table)
    {
        String subTableName = table.getSubTableName(); // 获取子表名
        if (StringUtils.isNotEmpty(subTableName)) // 如果配置了子表名
        {
            table.setSubTable(genTableMapper.selectGenTableByName(subTableName)); // 查询子表信息
        }
    }

    /**
     * 设置代码生成其他选项值
     * 
     * @param genTable 设置后的生成对象
     */
    // 从options JSON中提取树/菜单配置并设置到表对象
    public void setTableFromOptions(GenTable genTable)
    {
        JSONObject paramsObj = JSON.parseObject(genTable.getOptions()); // 解析options JSON
        if (StringUtils.isNotNull(paramsObj)) // 如果有配置
        {
            String treeCode = paramsObj.getString(GenConstants.TREE_CODE); // 树编码字段
            String treeParentCode = paramsObj.getString(GenConstants.TREE_PARENT_CODE); // 树父编码字段
            String treeName = paramsObj.getString(GenConstants.TREE_NAME); // 树名称字段
            String parentMenuId = paramsObj.getString(GenConstants.PARENT_MENU_ID); // 父菜单ID
            String parentMenuName = paramsObj.getString(GenConstants.PARENT_MENU_NAME); // 父菜单名称

            genTable.setTreeCode(treeCode); // 设置树编码
            genTable.setTreeParentCode(treeParentCode); // 设置树父编码
            genTable.setTreeName(treeName); // 设置树名称
            genTable.setParentMenuId(parentMenuId); // 设置父菜单ID
            genTable.setParentMenuName(parentMenuName); // 设置父菜单名称
        }
    }

    /**
     * 获取代码生成地址
     * 
     * @param table 业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    // 计算生成文件的绝对路径
    public static String getGenPath(GenTable table, String template)
    {
        String genPath = table.getGenPath(); // 获取配置的生成路径
        if (StringUtils.equals(genPath, "/")) // 如果路径为根目录
        {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table); // 使用项目根目录
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table); // 使用自定义路径拼接文件名
    }
}
