// 包声明
package com.debuff.common.utils.poi;

// 导入IO、反射、日期、集合、POI、Servlet等相关类
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.debuff.common.annotation.Excel;
import com.debuff.common.annotation.Excel.ColumnType;
import com.debuff.common.annotation.Excel.Type;
import com.debuff.common.annotation.Excels;
import com.debuff.common.config.RuoYiConfig;
import com.debuff.common.core.domain.AjaxResult;
import com.debuff.common.core.text.Convert;
import com.debuff.common.exception.UtilException;
import com.debuff.common.utils.DateUtils;
import com.debuff.common.utils.DictUtils;
import com.debuff.common.utils.StringUtils;
import com.debuff.common.utils.file.FileTypeUtils;
import com.debuff.common.utils.file.FileUtils;
import com.debuff.common.utils.file.ImageUtils;
import com.debuff.common.utils.reflect.ReflectUtils;

// Excel导入导出工具类，基于@Excel注解实现字段映射
public class ExcelUtil<T>
{
    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    // 公式正则（匹配=-+@开头的表达式，防CSV注入）
    public static final String FORMULA_REGEX_STR = "=|-|\\+|@";

    // 公式触发字符数组
    public static final String[] FORMULA_STR = { "=", "-", "+", "@" };

    // 字典类型缓存Map，避免重复查询缓存
    public Map<String, String> sysDictMap = new HashMap<String, String>();

    // 每个sheet最大行数
    public static final int sheetSize = 65536;

    // 工作表名称
    private String sheetName;

    // 操作类型（EXPORT导出/IMPORT导入模板）
    private Type type;

    // 工作簿对象
    private Workbook wb;

    // 工作表对象
    private Sheet sheet;

    // 样式缓存Map
    private Map<String, CellStyle> styles;

    // 导入导出数据列表
    private List<T> list;

    // 注解字段列表
    private List<Object[]> fields;

    // 当前行号
    private int rownum;

    // 标题
    private String title;

    // 最大行高
    private short maxHeight;

    // 合并区域最后行号
    private int subMergedLastRowNum = 0;

    // 合并区域起始行号
    private int subMergedFirstRowNum = 1;

    // 子列表的getter方法
    private Method subMethod;

    // 子列表的字段列表
    private List<Field> subFields;

    // 统计列的合计值Map（列索引 -> 合计值）
    private Map<Integer, Double> statistics = new HashMap<Integer, Double>();

    // 数字格式化（保留两位小数）
    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

    // 泛型实体类Class
    public Class<T> clazz;

    // 需要排除的列属性名数组
    public String[] excludeFields;

    // 构造函数，传入实体类Class
    public ExcelUtil(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    // 隐藏指定列（导出时不显示）
    public void hideColumn(String... fields)
    {
        this.excludeFields = fields;
    }

    // 初始化Excel（设置数据列表、sheet名、标题、类型，创建字段和工作簿）
    public void init(List<T> list, String sheetName, String title, Type type)
    {
        if (list == null) // 空列表则创建空列表
        {
            list = new ArrayList<T>();
        }
        this.list = list;
        this.sheetName = sheetName;
        this.type = type;
        this.title = title;
        createExcelField(); // 创建字段注解列表
        createWorkbook(); // 创建工作簿
        createTitle(); // 创建标题行
        createSubHead(); // 创建子列表表头
    }

    // 创建Excel第一行标题（合并单元格）
    public void createTitle()
    {
        if (StringUtils.isNotEmpty(title)) // 有标题才创建
        {
            subMergedFirstRowNum++; // 合并起始行+1
            subMergedLastRowNum++; // 合并结束行+1
            int titleLastCol = this.fields.size() - 1; // 标题合并的最后列
            if (isSubList()) // 有子列表则加上子字段数
            {
                titleLastCol = titleLastCol + subFields.size() - 1;
            }
            Row titleRow = sheet.createRow(rownum == 0 ? rownum++ : 0); // 创建标题行
            titleRow.setHeightInPoints(30); // 设置行高30pt
            Cell titleCell = titleRow.createCell(0); // 创建标题单元格
            titleCell.setCellStyle(styles.get("title")); // 设置标题样式
            titleCell.setCellValue(title); // 设置标题文本
            // 合并标题单元格
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), titleRow.getRowNum(), titleLastCol));
        }
    }

    // 创建子列表的表头行
    public void createSubHead()
    {
        if (isSubList()) // 有子列表才创建
        {
            subMergedFirstRowNum++;
            subMergedLastRowNum++;
            Row subRow = sheet.createRow(rownum); // 创建子表头行
            int excelNum = 0;
            // 遍历主字段写入表头
            for (Object[] objects : fields)
            {
                Excel attr = (Excel) objects[1]; // 获取Excel注解
                Cell headCell1 = subRow.createCell(excelNum); // 创建表头单元格
                headCell1.setCellValue(attr.name()); // 设置列名
                headCell1.setCellStyle(styles.get(StringUtils.format("header_{}_{}", attr.headerColor(), attr.headerBackgroundColor()))); // 设置表头样式
                excelNum++;
            }
            int headFirstRow = excelNum - 1; // 子列表表头起始列
            int headLastRow = headFirstRow + subFields.size() - 1; // 子列表表头结束列
            if (headLastRow > headFirstRow) // 需要合并则合并
            {
                sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, headFirstRow, headLastRow));
            }
            rownum++; // 行号+1
        }
    }

    // 导入Excel（默认第一个sheet）
    public List<T> importExcel(InputStream is)
    {
        List<T> list = null;
        try
        {
            list = importExcel(is, 0); // 标题占0行
        }
        catch (Exception e)
        {
            log.error("导入Excel异常{}", e.getMessage());
            throw new UtilException(e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(is); // 关闭输入流
        }
        return list;
    }

    // 导入Excel（指定标题行数）
    public List<T> importExcel(InputStream is, int titleNum) throws Exception
    {
        return importExcel(StringUtils.EMPTY, is, titleNum);
    }

    // 导入Excel核心方法（指定sheet名、标题行数）
    public List<T> importExcel(String sheetName, InputStream is, int titleNum) throws Exception
    {
        this.type = Type.IMPORT; // 设置为导入类型
        this.wb = WorkbookFactory.create(is); // 创建工作簿
        List<T> list = new ArrayList<T>();
        // 如果指定sheet名则取指定sheet，否则取第一个
        Sheet sheet = StringUtils.isNotEmpty(sheetName) ? wb.getSheet(sheetName) : wb.getSheetAt(0);
        if (sheet == null) // sheet不存在
        {
            throw new IOException("文件sheet不存在");
        }
        boolean isXSSFWorkbook = !(wb instanceof HSSFWorkbook); // 判断是否为xlsx格式
        Map<String, PictureData> pictures; // 图片数据Map
        if (isXSSFWorkbook) // xlsx格式获取图片
        {
            pictures = getSheetPictures07((XSSFSheet) sheet, (XSSFWorkbook) wb);
        }
        else // xls格式获取图片
        {
            pictures = getSheetPictures03((HSSFSheet) sheet, (HSSFWorkbook) wb);
        }
        // 获取最后一个非空行的行下标
        int rows = sheet.getLastRowNum();
        if (rows > 0) // 有数据行
        {
            // 定义map存放excel列序号和field的映射
            Map<String, Integer> cellMap = new HashMap<String, Integer>();
            // 获取表头行
            Row heard = sheet.getRow(titleNum);
            // 遍历表头单元格，建立列名->列索引的映射
            for (int i = 0; i < heard.getPhysicalNumberOfCells(); i++)
            {
                Cell cell = heard.getCell(i);
                if (StringUtils.isNotNull(cell))
                {
                    String value = this.getCellValue(heard, i).toString(); // 获取列名
                    cellMap.put(value, i); // 列名->列索引
                }
                else
                {
                    cellMap.put(null, i);
                }
            }
            // 获取类的所有注解字段
            List<Object[]> fields = this.getFields();
            // 建立列索引->字段注解的映射
            Map<Integer, Object[]> fieldsMap = new HashMap<Integer, Object[]>();
            for (Object[] objects : fields)
            {
                Excel attr = (Excel) objects[1]; // 获取Excel注解
                Integer column = cellMap.get(attr.name()); // 根据注解名找列索引
                if (column != null) // 找到了则放入映射
                {
                    fieldsMap.put(column, objects);
                }
            }
            // 从标题下一行开始遍历数据行
            for (int i = titleNum + 1; i <= rows; i++)
            {
                // 从第2行开始取数据,默认第一行是表头.
                Row row = sheet.getRow(i);
                // 判断当前行是否是空行
                if (isRowEmpty(row))
                {
                    continue; // 空行跳过
                }
                T entity = null;
                // 遍历每个字段映射
                for (Map.Entry<Integer, Object[]> entry : fieldsMap.entrySet())
                {
                    Object val = this.getCellValue(row, entry.getKey()); // 获取单元格值

                    // 如果不存在实例则新建
                    entity = (entity == null ? clazz.newInstance() : entity);
                    // 从map中得到对应列的field
                    Field field = (Field) entry.getValue()[0];
                    Excel attr = (Excel) entry.getValue()[1];
                    // 取得字段类型
                    Class<?> fieldType = field.getType();
                    // 根据字段类型进行类型转换
                    if (String.class == fieldType) // String类型
                    {
                        String s = Convert.toStr(val);
                        if (StringUtils.endsWith(s, ".0")) // 去掉.0后缀
                        {
                            val = StringUtils.substringBefore(s, ".0");
                        }
                        else
                        {
                            String dateFormat = field.getAnnotation(Excel.class).dateFormat();
                            if (StringUtils.isNotEmpty(dateFormat)) // 有日期格式则格式化
                            {
                                val = parseDateToStr(dateFormat, val);
                            }
                            else
                            {
                                val = Convert.toStr(val);
                            }
                        }
                    }
                    else if ((Integer.TYPE == fieldType || Integer.class == fieldType) && StringUtils.isNumeric(Convert.toStr(val))) // Integer类型
                    {
                        val = Convert.toInt(val);
                    }
                    else if ((Long.TYPE == fieldType || Long.class == fieldType) && StringUtils.isNumeric(Convert.toStr(val))) // Long类型
                    {
                        val = Convert.toLong(val);
                    }
                    else if (Double.TYPE == fieldType || Double.class == fieldType) // Double类型
                    {
                        val = Convert.toDouble(val);
                    }
                    else if (Float.TYPE == fieldType || Float.class == fieldType) // Float类型
                    {
                        val = Convert.toFloat(val);
                    }
                    else if (BigDecimal.class == fieldType) // BigDecimal类型
                    {
                        val = Convert.toBigDecimal(val);
                    }
                    else if (Date.class == fieldType) // Date类型
                    {
                        if (val instanceof String) // 字符串转日期
                        {
                            val = DateUtils.parseDate(val);
                        }
                        else if (val instanceof Double) // Excel数字日期转Java日期
                        {
                            val = DateUtil.getJavaDate((Double) val);
                        }
                    }
                    else if (Boolean.TYPE == fieldType || Boolean.class == fieldType) // Boolean类型
                    {
                        val = Convert.toBool(val, false);
                    }
                    if (StringUtils.isNotNull(fieldType))
                    {
                        String propertyName = field.getName(); // 属性名
                        if (StringUtils.isNotEmpty(attr.targetAttr())) // 有目标属性则拼接
                        {
                            propertyName = field.getName() + "." + attr.targetAttr();
                        }
                        if (StringUtils.isNotEmpty(attr.readConverterExp())) // 有反向转换表达式
                        {
                            val = reverseByExp(Convert.toStr(val), attr.readConverterExp(), attr.separator());
                        }
                        else if (StringUtils.isNotEmpty(attr.dictType())) // 有字典类型
                        {
                            val = reverseDictByExp(Convert.toStr(val), attr.dictType(), attr.separator());
                        }
                        else if (!attr.handler().equals(ExcelHandlerAdapter.class)) // 有自定义处理器
                        {
                            val = dataFormatHandlerAdapter(val, attr, null);
                        }
                        else if (ColumnType.IMAGE == attr.cellType() && StringUtils.isNotEmpty(pictures)) // 图片类型
                        {
                            PictureData image = pictures.get(row.getRowNum() + "_" + entry.getKey()); // 获取图片数据
                            if (image == null)
                            {
                                val = "";
                            }
                            else
                            {
                                byte[] data = image.getData(); // 获取图片字节
                                val = FileUtils.writeImportBytes(data); // 保存图片并返回路径
                            }
                        }
                        ReflectUtils.invokeSetter(entity, propertyName, val); // 反射设置属性值
                    }
                }
                list.add(entity); // 添加到结果列表
            }
        }
        return list;
    }

    // 导出数据列表到Excel（返回AjaxResult）
    public AjaxResult exportExcel(List<T> list, String sheetName)
    {
        return exportExcel(list, sheetName, StringUtils.EMPTY);
    }

    // 导出数据列表到Excel（带标题，返回AjaxResult）
    public AjaxResult exportExcel(List<T> list, String sheetName, String title)
    {
        this.init(list, sheetName, title, Type.EXPORT); // 初始化
        return exportExcel();
    }

    // 导出数据列表到Excel（通过response输出）
    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName)
    {
        exportExcel(response, list, sheetName, StringUtils.EMPTY);
    }

    // 导出数据列表到Excel（通过response输出，带标题）
    public void exportExcel(HttpServletResponse response, List<T> list, String sheetName, String title)
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // 设置响应类型
        response.setCharacterEncoding("utf-8"); // 设置编码
        this.init(list, sheetName, title, Type.EXPORT); // 初始化
        exportExcel(response);
    }

    // 生成导入模板（返回AjaxResult）
    public AjaxResult importTemplateExcel(String sheetName)
    {
        return importTemplateExcel(sheetName, StringUtils.EMPTY);
    }

    // 生成导入模板（带标题，返回AjaxResult）
    public AjaxResult importTemplateExcel(String sheetName, String title)
    {
        this.init(null, sheetName, title, Type.IMPORT); // 初始化（数据为null）
        return exportExcel();
    }

    // 生成导入模板（通过response输出）
    public void importTemplateExcel(HttpServletResponse response, String sheetName)
    {
        importTemplateExcel(response, sheetName, StringUtils.EMPTY);
    }

    // 生成导入模板（通过response输出，带标题）
    public void importTemplateExcel(HttpServletResponse response, String sheetName, String title)
    {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // 设置响应类型
        response.setCharacterEncoding("utf-8"); // 设置编码
        this.init(null, sheetName, title, Type.IMPORT); // 初始化
        exportExcel(response);
    }

    // 导出Excel到response输出流
    public void exportExcel(HttpServletResponse response)
    {
        try
        {
            writeSheet(); // 写入数据到sheet
            wb.write(response.getOutputStream()); // 输出到response
        }
        catch (Exception e)
        {
            log.error("导出Excel异常{}", e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(wb); // 关闭工作簿
        }
    }

    // 导出Excel到文件（返回AjaxResult含文件名）
    public AjaxResult exportExcel()
    {
        OutputStream out = null;
        try
        {
            writeSheet(); // 写入数据到sheet
            String filename = encodingFilename(sheetName); // 生成文件名
            out = new FileOutputStream(getAbsoluteFile(filename)); // 创建文件输出流
            wb.write(out); // 写入工作簿
            return AjaxResult.success(filename); // 返回文件名
        }
        catch (Exception e)
        {
            log.error("导出Excel异常{}", e.getMessage());
            throw new UtilException("导出Excel失败，请联系网站管理员！");
        }
        finally
        {
            IOUtils.closeQuietly(wb); // 关闭工作簿
            IOUtils.closeQuietly(out); // 关闭输出流
        }
    }

    // 写入数据到Sheet（包含创建sheet、表头、数据行）
    public void writeSheet()
    {
        // 计算需要多少个sheet（按sheetSize分页）
        int sheetNo = Math.max(1, (int) Math.ceil(list.size() * 1.0 / sheetSize));
        for (int index = 0; index < sheetNo; index++)
        {
            createSheet(sheetNo, index); // 创建工作表

            // 创建表头行
            Row row = sheet.createRow(rownum);
            int column = 0;
            // 写入各字段的列头名称
            for (Object[] os : fields)
            {
                Field field = (Field) os[0];
                Excel excel = (Excel) os[1];
                if (Collection.class.isAssignableFrom(field.getType())) // 子列表类型
                {
                    for (Field subField : subFields) // 遍历子字段
                    {
                        Excel subExcel = subField.getAnnotation(Excel.class);
                        this.createHeadCell(subExcel, row, column++); // 创建子列表表头
                    }
                }
                else // 普通字段
                {
                    this.createHeadCell(excel, row, column++); // 创建表头
                }
            }
            if (Type.EXPORT.equals(type)) // 导出类型才填充数据
            {
                fillExcelData(index, row); // 填充数据
                addStatisticsRow(); // 添加统计行
            }
        }
    }

    // 填充Excel数据行
    @SuppressWarnings("unchecked")
    public void fillExcelData(int index, Row row)
    {
        int startNo = index * sheetSize; // 当前sheet起始数据索引
        int endNo = Math.min(startNo + sheetSize, list.size()); // 当前sheet结束数据索引
        int rowNo = (1 + rownum) - startNo; // 当前行号
        for (int i = startNo; i < endNo; i++)
        {
            rowNo = isSubList() ? (i > 1 ? rowNo + 1 : rowNo + i) : i + 1 + rownum - startNo;
            row = sheet.createRow(rowNo); // 创建数据行
            // 得到导出对象
            T vo = (T) list.get(i);
            Collection<?> subList = null; // 子列表数据
            if (isSubList()) // 有子列表
            {
                if (isSubListValue(vo)) // 子列表不为空
                {
                    subList = getListCellValue(vo); // 获取子列表
                    subMergedLastRowNum = subMergedLastRowNum + subList.size(); // 更新合并结束行
                }
                else // 子列表为空
                {
                    subMergedFirstRowNum++;
                    subMergedLastRowNum++;
                }
            }
            int column = 0;
            // 遍历字段写入数据
            for (Object[] os : fields)
            {
                Field field = (Field) os[0];
                Excel excel = (Excel) os[1];
                if (Collection.class.isAssignableFrom(field.getType()) && StringUtils.isNotNull(subList)) // 子列表字段
                {
                    boolean subFirst = false; // 是否是子列表第一个元素
                    for (Object obj : subList) // 遍历子列表
                    {
                        if (subFirst) // 非第一个元素需要新建行
                        {
                            rowNo++;
                            row = sheet.createRow(rowNo);
                        }
                        // 获取子对象的Excel注解字段
                        List<Field> subFields = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), Excel.class);
                        int subIndex = 0;
                        for (Field subField : subFields)
                        {
                            if (subField.isAnnotationPresent(Excel.class))
                            {
                                subField.setAccessible(true); // 设置可访问
                                Excel attr = subField.getAnnotation(Excel.class);
                                this.addCell(attr, row, (T) obj, subField, column + subIndex); // 添加单元格
                            }
                            subIndex++;
                        }
                        subFirst = true;
                    }
                    this.subMergedFirstRowNum = this.subMergedFirstRowNum + subList.size(); // 更新合并起始行
                }
                else // 普通字段
                {
                    this.addCell(excel, row, vo, field, column++); // 添加单元格
                }
            }
        }
    }

    // 创建Excel样式Map（标题、数据、合计、表头、数据列样式）
    private Map<String, CellStyle> createStyles(Workbook wb)
    {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        // 创建标题样式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        Font titleFont = wb.createFont(); // 创建字体
        titleFont.setFontName("Arial"); // 字体名称
        titleFont.setFontHeightInPoints((short) 16); // 字号16
        titleFont.setBold(true); // 加粗
        style.setFont(titleFont);
        styles.put("title", style); // 放入样式Map

        // 创建数据行样式
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN); // 右边框
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN); // 左边框
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN); // 上边框
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN); // 下边框
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10); // 字号10
        style.setFont(dataFont);
        styles.put("data", style);

        // 创建合计行样式
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font totalFont = wb.createFont();
        totalFont.setFontName("Arial");
        totalFont.setFontHeightInPoints((short) 10);
        style.setFont(totalFont);
        styles.put("total", style);

        styles.putAll(annotationHeaderStyles(wb, styles)); // 添加注解定义的表头样式

        styles.putAll(annotationDataStyles(wb)); // 添加注解定义的数据列样式

        return styles;
    }

    // 根据@Excel注解创建表头样式（按前景色+背景色组合去重）
    private Map<String, CellStyle> annotationHeaderStyles(Workbook wb, Map<String, CellStyle> styles)
    {
        Map<String, CellStyle> headerStyles = new HashMap<String, CellStyle>();
        for (Object[] os : fields) // 遍历所有字段
        {
            Excel excel = (Excel) os[1];
            String key = StringUtils.format("header_{}_{}", excel.headerColor(), excel.headerBackgroundColor()); // 样式key
            if (!headerStyles.containsKey(key)) // 不重复创建
            {
                CellStyle style = wb.createCellStyle();
                style.cloneStyleFrom(styles.get("data")); // 克隆数据样式
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setFillForegroundColor(excel.headerBackgroundColor().index); // 设置背景色
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 实心填充
                Font headerFont = wb.createFont();
                headerFont.setFontName("Arial");
                headerFont.setFontHeightInPoints((short) 10);
                headerFont.setBold(true); // 表头字体加粗
                headerFont.setColor(excel.headerColor().index); // 字体颜色
                style.setFont(headerFont);
                headerStyles.put(key, style);
            }
        }
        return headerStyles;
    }

    // 根据@Excel注解创建数据列样式（按对齐+颜色+背景色组合去重）
    private Map<String, CellStyle> annotationDataStyles(Workbook wb)
    {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        for (Object[] os : fields)
        {
            Excel excel = (Excel) os[1];
            String key = StringUtils.format("data_{}_{}_{}", excel.align(), excel.color(), excel.backgroundColor()); // 样式key
            if (!styles.containsKey(key))
            {
                CellStyle style = wb.createCellStyle();
                style.setAlignment(excel.align()); // 设置对齐方式
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setBorderRight(BorderStyle.THIN);
                style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
                style.setBorderLeft(BorderStyle.THIN);
                style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
                style.setBorderTop(BorderStyle.THIN);
                style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
                style.setBorderBottom(BorderStyle.THIN);
                style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setFillForegroundColor(excel.backgroundColor().getIndex()); // 背景色
                Font dataFont = wb.createFont();
                dataFont.setFontName("Arial");
                dataFont.setFontHeightInPoints((short) 10);
                dataFont.setColor(excel.color().index); // 字体颜色
                style.setFont(dataFont);
                styles.put(key, style);
            }
        }
        return styles;
    }

    // 创建表头单元格（设置列名、数据验证、样式）
    public Cell createHeadCell(Excel attr, Row row, int column)
    {
        // 创建列
        Cell cell = row.createCell(column);
        // 写入列信息
        cell.setCellValue(attr.name()); // 设置列名
        setDataValidation(attr, row, column); // 设置数据验证（下拉框/提示）
        cell.setCellStyle(styles.get(StringUtils.format("header_{}_{}", attr.headerColor(), attr.headerBackgroundColor()))); // 设置表头样式
        if (isSubList()) // 有子列表
        {
            // 填充默认样式，防止合并单元格样式失效
            sheet.setDefaultColumnStyle(column, styles.get(StringUtils.format("data_{}_{}_{}", attr.align(), attr.color(), attr.backgroundColor())));
            if (attr.needMerge()) // 需要合并
            {
                sheet.addMergedRegion(new CellRangeAddress(rownum - 1, rownum, column, column)); // 合并单元格
            }
        }
        return cell;
    }

    // 设置单元格值（根据列类型设置String/Numeric/Image）
    public void setCellVo(Object value, Excel attr, Cell cell)
    {
        if (ColumnType.STRING == attr.cellType()) // 字符串类型
        {
            String cellValue = Convert.toStr(value);
            // 对于任何以表达式触发字符 =-+@开头的单元格，直接使用tab字符作为前缀，防止CSV注入。
            if (StringUtils.startsWithAny(cellValue, FORMULA_STR))
            {
                cellValue = RegExUtils.replaceFirst(cellValue, FORMULA_REGEX_STR, "\t$0");
            }
            if (value instanceof Collection && StringUtils.equals("[]", cellValue)) // 空集合
            {
                cellValue = StringUtils.EMPTY;
            }
            cell.setCellValue(StringUtils.isNull(cellValue) ? attr.defaultValue() : cellValue + attr.suffix()); // 设置值+后缀
        }
        else if (ColumnType.NUMERIC == attr.cellType()) // 数字类型
        {
            if (StringUtils.isNotNull(value))
            {
                cell.setCellValue(StringUtils.contains(Convert.toStr(value), ".") ? Convert.toDouble(value) : Convert.toInt(value)); // 整数或小数
            }
        }
        else if (ColumnType.IMAGE == attr.cellType()) // 图片类型
        {
            // 创建图片锚点
            ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + 1), cell.getRow().getRowNum() + 1);
            String imagePath = Convert.toStr(value); // 图片路径
            if (StringUtils.isNotEmpty(imagePath))
            {
                byte[] data = ImageUtils.getImage(imagePath); // 读取图片字节
                getDrawingPatriarch(cell.getSheet()).createPicture(anchor,
                        cell.getSheet().getWorkbook().addPicture(data, getImageType(data))); // 插入图片
            }
        }
    }

    // 获取或创建画布对象
    public static Drawing<?> getDrawingPatriarch(Sheet sheet)
    {
        if (sheet.getDrawingPatriarch() == null) // 不存在则创建
        {
            sheet.createDrawingPatriarch();
        }
        return sheet.getDrawingPatriarch();
    }

    // 根据字节获取图片类型（JPEG/PNG）
    public int getImageType(byte[] value)
    {
        String type = FileTypeUtils.getFileExtendName(value); // 获取文件扩展名
        if ("JPG".equalsIgnoreCase(type))
        {
            return Workbook.PICTURE_TYPE_JPEG;
        }
        else if ("PNG".equalsIgnoreCase(type))
        {
            return Workbook.PICTURE_TYPE_PNG;
        }
        return Workbook.PICTURE_TYPE_JPEG; // 默认JPEG
    }

    // 设置列宽和数据验证（下拉框/提示信息）
    public void setDataValidation(Excel attr, Row row, int column)
    {
        if (attr.name().indexOf("注：") >= 0) // 备注列加宽
        {
            sheet.setColumnWidth(column, 6000);
        }
        else
        {
            // 设置列宽（根据注解width属性）
            sheet.setColumnWidth(column, (int) ((attr.width() + 0.72) * 256));
        }
        if (StringUtils.isNotEmpty(attr.prompt()) || attr.combo().length > 0) // 有提示或下拉框
        {
            if (attr.combo().length > 15 || StringUtils.join(attr.combo()).length() > 255) // 下拉项过多
            {
                // 使用隐藏sheet存储下拉数据
                setXSSFValidationWithHidden(sheet, attr.combo(), attr.prompt(), 1, 100, column, column);
            }
            else
            {
                // 直接设置提示或下拉验证
                setPromptOrValidation(sheet, attr.combo(), attr.prompt(), 1, 100, column, column);
            }
        }
    }

    // 添加数据单元格（设置值、样式、转换、统计）
    public Cell addCell(Excel attr, Row row, T vo, Field field, int column)
    {
        Cell cell = null;
        try
        {
            // 设置行高
            row.setHeight(maxHeight);
            // 根据Excel注解决定是否导出该列
            if (attr.isExport())
            {
                // 创建cell
                cell = row.createCell(column);
                // 子列表需要合并单元格
                if (isSubListValue(vo) && getListCellValue(vo).size() > 1 && attr.needMerge())
                {
                    CellRangeAddress cellAddress = new CellRangeAddress(subMergedFirstRowNum, subMergedLastRowNum, column, column);
                    sheet.addMergedRegion(cellAddress);
                }
                cell.setCellStyle(styles.get(StringUtils.format("data_{}_{}_{}", attr.align(), attr.color(), attr.backgroundColor()))); // 设置数据样式

                // 获取字段值（支持嵌套属性）
                Object value = getTargetValue(vo, field, attr);
                String dateFormat = attr.dateFormat(); // 日期格式
                String readConverterExp = attr.readConverterExp(); // 转换表达式
                String separator = attr.separator(); // 分隔符
                String dictType = attr.dictType(); // 字典类型
                if (StringUtils.isNotEmpty(dateFormat) && StringUtils.isNotNull(value)) // 日期格式化
                {
                    cell.setCellValue(parseDateToStr(dateFormat, value));
                }
                else if (StringUtils.isNotEmpty(readConverterExp) && StringUtils.isNotNull(value)) // 表达式转换
                {
                    cell.setCellValue(convertByExp(Convert.toStr(value), readConverterExp, separator));
                }
                else if (StringUtils.isNotEmpty(dictType) && StringUtils.isNotNull(value)) // 字典转换
                {
                    if (!sysDictMap.containsKey(dictType + value)) // 缓存未命中则查询
                    {
                        String lable = convertDictByExp(Convert.toStr(value), dictType, separator);
                        sysDictMap.put(dictType + value, lable); // 放入缓存
                    }
                    cell.setCellValue(sysDictMap.get(dictType + value)); // 使用缓存值
                }
                else if (value instanceof BigDecimal && -1 != attr.scale()) // BigDecimal精度处理
                {
                    cell.setCellValue((((BigDecimal) value).setScale(attr.scale(), attr.roundingMode())).doubleValue());
                }
                else if (!attr.handler().equals(ExcelHandlerAdapter.class)) // 自定义处理器
                {
                    cell.setCellValue(dataFormatHandlerAdapter(value, attr, cell));
                }
                else // 默认处理
                {
                    // 设置列类型
                    setCellVo(value, attr, cell);
                }
                addStatisticsData(column, Convert.toStr(value), attr); // 添加统计数据
            }
        }
        catch (Exception e)
        {
            log.error("导出Excel失败{}", e);
        }
        return cell;
    }

    // 设置单元格数据验证（下拉框或提示信息）
    public void setPromptOrValidation(Sheet sheet, String[] textlist, String promptContent, int firstRow, int endRow,
            int firstCol, int endCol)
    {
        DataValidationHelper helper = sheet.getDataValidationHelper(); // 获取验证帮助类
        // 创建验证约束（下拉列表或自定义）
        DataValidationConstraint constraint = textlist.length > 0 ? helper.createExplicitListConstraint(textlist) : helper.createCustomConstraint("DD1");
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol); // 验证区域
        DataValidation dataValidation = helper.createValidation(constraint, regions); // 创建验证对象
        if (StringUtils.isNotEmpty(promptContent)) // 有提示信息
        {
            // 如果设置了提示信息则鼠标放上去提示
            dataValidation.createPromptBox("", promptContent);
            dataValidation.setShowPromptBox(true);
        }
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) // xlsx格式
        {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        }
        else // xls格式
        {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation); // 添加验证到sheet
    }

    // 设置下拉框验证（使用隐藏sheet存储大量下拉数据）
    public void setXSSFValidationWithHidden(Sheet sheet, String[] textlist, String promptContent, int firstRow, int endRow, int firstCol, int endCol)
    {
        String hideSheetName = "combo_" + firstCol + "_" + endCol; // 隐藏sheet名称
        Sheet hideSheet = wb.createSheet(hideSheetName); // 创建隐藏sheet存储下拉数据
        // 将下拉数据写入隐藏sheet
        for (int i = 0; i < textlist.length; i++)
        {
            hideSheet.createRow(i).createCell(0).setCellValue(textlist[i]);
        }
        // 创建名称定义，供其他单元格引用
        Name name = wb.createName();
        name.setNameName(hideSheetName + "_data"); // 名称
        name.setRefersToFormula(hideSheetName + "!$A$1:$A$" + textlist.length); // 引用公式
        DataValidationHelper helper = sheet.getDataValidationHelper();
        // 加载下拉列表内容（通过公式引用隐藏sheet）
        DataValidationConstraint constraint = helper.createFormulaListConstraint(hideSheetName + "_data");
        // 设置数据有效性区域
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 创建数据验证对象
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        if (StringUtils.isNotEmpty(promptContent)) // 有提示信息
        {
            dataValidation.createPromptBox("", promptContent);
            dataValidation.setShowPromptBox(true);
        }
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation)
        {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        }
        else
        {
            dataValidation.setSuppressDropDownArrow(false);
        }

        sheet.addValidationData(dataValidation); // 添加验证
        // 设置隐藏sheet为不可见
        wb.setSheetHidden(wb.getSheetIndex(hideSheet), true);
    }

    // 正向解析导出值（如 0=男,1=女 -> 男/女）
    public static String convertByExp(String propertyValue, String converterExp, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(","); // 分割转换源（如 0=男,1=女）
        for (String item : convertSource)
        {
            String[] itemArray = item.split("="); // 分割键值对
            if (StringUtils.containsAny(propertyValue, separator)) // 多值（含分隔符）
            {
                for (String value : propertyValue.split(separator))
                {
                    if (itemArray[0].equals(value)) // 键匹配
                    {
                        propertyString.append(itemArray[1] + separator); // 拼接值
                        break;
                    }
                }
            }
            else // 单值
            {
                if (itemArray[0].equals(propertyValue))
                {
                    return itemArray[1]; // 直接返回转换后的值
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator); // 去掉末尾分隔符
    }

    // 反向解析值（如 男=0,女=1 -> 男/女 转为 0/1）
    public static String reverseByExp(String propertyValue, String converterExp, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        String[] convertSource = converterExp.split(","); // 分割转换源
        for (String item : convertSource)
        {
            String[] itemArray = item.split("="); // 分割键值对
            if (StringUtils.containsAny(propertyValue, separator)) // 多值
            {
                for (String value : propertyValue.split(separator))
                {
                    if (itemArray[1].equals(value)) // 值匹配（反向）
                    {
                        propertyString.append(itemArray[0] + separator); // 拼接键
                        break;
                    }
                }
            }
            else // 单值
            {
                if (itemArray[1].equals(propertyValue))
                {
                    return itemArray[0]; // 返回键
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    // 解析字典值为标签（如 0 -> 男）
    public static String convertDictByExp(String dictValue, String dictType, String separator)
    {
        return DictUtils.getDictLabel(dictType, dictValue, separator);
    }

    // 反向解析字典标签为值（如 男 -> 0）
    public static String reverseDictByExp(String dictLabel, String dictType, String separator)
    {
        return DictUtils.getDictValue(dictType, dictLabel, separator);
    }

    // 使用自定义数据处理器格式化值
    public String dataFormatHandlerAdapter(Object value, Excel excel, Cell cell)
    {
        try
        {
            Object instance = excel.handler().newInstance(); // 创建处理器实例
            // 获取format方法
            Method formatMethod = excel.handler().getMethod("format", new Class[] { Object.class, String[].class, Cell.class, Workbook.class });
            value = formatMethod.invoke(instance, value, excel.args(), cell, this.wb); // 调用format方法
        }
        catch (Exception e)
        {
            log.error("不能格式化数据 " + excel.handler(), e.getMessage());
        }
        return Convert.toStr(value);
    }

    // 累加统计列的数据
    private void addStatisticsData(Integer index, String text, Excel entity)
    {
        if (entity != null && entity.isStatistics()) // 该列需要统计
        {
            Double temp = 0D;
            if (!statistics.containsKey(index)) // 首次初始化
            {
                statistics.put(index, temp);
            }
            try
            {
                temp = Double.valueOf(text); // 转为数字
            }
            catch (NumberFormatException e) // 非数字则为0
            {
            }
            statistics.put(index, statistics.get(index) + temp); // 累加
        }
    }

    // 创建合计统计行
    public void addStatisticsRow()
    {
        if (statistics.size() > 0) // 有统计列
        {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1); // 创建新行
            Set<Integer> keys = statistics.keySet(); // 获取统计列索引
            Cell cell = row.createCell(0); // 第一列写"合计"
            cell.setCellStyle(styles.get("total")); // 合计样式
            cell.setCellValue("合计");

            for (Integer key : keys) // 遍历统计列
            {
                cell = row.createCell(key);
                cell.setCellStyle(styles.get("total"));
                cell.setCellValue(DOUBLE_FORMAT.format(statistics.get(key))); // 写入合计值
            }
            statistics.clear(); // 清空统计
        }
    }

    // 生成带UUID的文件名
    public String encodingFilename(String filename)
    {
        filename = UUID.randomUUID() + "_" + filename + ".xlsx"; // UUID前缀防重名
        return filename;
    }

    // 获取文件绝对路径（不存在则创建目录）
    public String getAbsoluteFile(String filename)
    {
        String downloadPath = RuoYiConfig.getDownloadPath() + filename; // 下载路径+文件名
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) // 父目录不存在
        {
            desc.getParentFile().mkdirs(); // 创建目录
        }
        return downloadPath;
    }

    // 获取对象属性值（支持嵌套属性如 user.name）
    private Object getTargetValue(T vo, Field field, Excel excel) throws Exception
    {
        Object o = field.get(vo); // 反射获取字段值
        if (StringUtils.isNotEmpty(excel.targetAttr())) // 有目标属性
        {
            String target = excel.targetAttr();
            if (target.contains(".")) // 多级嵌套
            {
                String[] targets = target.split("[.]"); // 按.分割
                for (String name : targets)
                {
                    o = getValue(o, name); // 逐级获取
                }
            }
            else // 一级嵌套
            {
                o = getValue(o, target);
            }
        }
        return o;
    }

    // 通过反射获取对象属性值
    private Object getValue(Object o, String name) throws Exception
    {
        if (StringUtils.isNotNull(o) && StringUtils.isNotEmpty(name))
        {
            Class<?> clazz = o.getClass();
            Field field = clazz.getDeclaredField(name); // 获取声明字段
            field.setAccessible(true); // 设置可访问
            o = field.get(o); // 获取值
        }
        return o;
    }

    // 创建Excel字段注解列表（按sort排序）
    private void createExcelField()
    {
        this.fields = getFields(); // 获取字段
        // 按注解的sort属性排序
        this.fields = this.fields.stream().sorted(Comparator.comparing(objects -> ((Excel) objects[1]).sort())).collect(Collectors.toList());
        this.maxHeight = getRowHeight(); // 获取最大行高
    }

    // 获取所有带@Excel/@Excels注解的字段信息
    public List<Object[]> getFields()
    {
        List<Object[]> fields = new ArrayList<Object[]>();
        List<Field> tempFields = new ArrayList<>();
        // 获取父类和当前类的所有字段
        tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        for (Field field : tempFields)
        {
            if (!ArrayUtils.contains(this.excludeFields, field.getName())) // 不在排除列表中
            {
                // 单注解 @Excel
                if (field.isAnnotationPresent(Excel.class))
                {
                    Excel attr = field.getAnnotation(Excel.class);
                    if (attr != null && (attr.type() == Type.ALL || attr.type() == type)) // 类型匹配
                    {
                        field.setAccessible(true);
                        fields.add(new Object[] { field, attr }); // 添加字段和注解
                    }
                    if (Collection.class.isAssignableFrom(field.getType())) // 集合类型（子列表）
                    {
                        subMethod = getSubMethod(field.getName(), clazz); // 获取getter方法
                        ParameterizedType pt = (ParameterizedType) field.getGenericType(); // 获取泛型参数
                        Class<?> subClass = (Class<?>) pt.getActualTypeArguments()[0];
                        this.subFields = FieldUtils.getFieldsListWithAnnotation(subClass, Excel.class); // 获取子类的Excel注解字段
                    }
                }

                // 多注解 @Excels
                if (field.isAnnotationPresent(Excels.class))
                {
                    Excels attrs = field.getAnnotation(Excels.class);
                    Excel[] excels = attrs.value();
                    for (Excel attr : excels)
                    {
                        if (!ArrayUtils.contains(this.excludeFields, field.getName() + "." + attr.targetAttr())
                                && (attr != null && (attr.type() == Type.ALL || attr.type() == type)))
                        {
                            field.setAccessible(true);
                            fields.add(new Object[] { field, attr });
                        }
                    }
                }
            }
        }
        return fields;
    }

    // 根据注解获取最大行高
    public short getRowHeight()
    {
        double maxHeight = 0;
        for (Object[] os : this.fields) // 遍历所有字段
        {
            Excel excel = (Excel) os[1];
            maxHeight = Math.max(maxHeight, excel.height()); // 取最大高度
        }
        return (short) (maxHeight * 20); // 转为POI行高单位
    }

    // 创建工作簿（使用SXSSFWorkbook流式写入，窗口大小500行）
    public void createWorkbook()
    {
        this.wb = new SXSSFWorkbook(500); // 流式工作簿
        this.sheet = wb.createSheet(); // 创建sheet
        wb.setSheetName(0, sheetName); // 设置sheet名
        this.styles = createStyles(wb); // 创建样式
    }

    // 创建工作表（数据超过sheetSize时创建多个sheet）
    public void createSheet(int sheetNo, int index)
    {
        // 设置工作表的名称
        if (sheetNo > 1 && index > 0) // 多sheet且非第一个
        {
            this.sheet = wb.createSheet(); // 创建新sheet
            this.createTitle(); // 创建标题
            wb.setSheetName(index, sheetName + index); // 设置sheet名（加序号）
        }
    }

    // 获取单元格值（支持数字、字符串、布尔、错误、日期类型）
    public Object getCellValue(Row row, int column)
    {
        if (row == null) // 行为空
        {
            return row;
        }
        Object val = "";
        try
        {
            Cell cell = row.getCell(column);
            if (StringUtils.isNotNull(cell))
            {
                if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) // 数字或公式
                {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell)) // 日期格式
                    {
                        val = DateUtil.getJavaDate((Double) val); // POI Excel 日期格式转换
                    }
                    else
                    {
                        if ((Double) val % 1 != 0) // 小数
                        {
                            val = new BigDecimal(val.toString());
                        }
                        else // 整数
                        {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                }
                else if (cell.getCellType() == CellType.STRING) // 字符串
                {
                    val = cell.getStringCellValue();
                }
                else if (cell.getCellType() == CellType.BOOLEAN) // 布尔
                {
                    val = cell.getBooleanCellValue();
                }
                else if (cell.getCellType() == CellType.ERROR) // 错误值
                {
                    val = cell.getErrorCellValue();
                }

            }
        }
        catch (Exception e)
        {
            return val;
        }
        return val;
    }

    // 判断行是否为空行
    private boolean isRowEmpty(Row row)
    {
        if (row == null) // 行为空
        {
            return true;
        }
        // 遍历所有单元格
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++)
        {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) // 有非空单元格
            {
                return false;
            }
        }
        return true; // 全部为空
    }

    // 获取Excel2003（xls）格式的图片数据
    public static Map<String, PictureData> getSheetPictures03(HSSFSheet sheet, HSSFWorkbook workbook)
    {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<String, PictureData>();
        List<HSSFPictureData> pictures = workbook.getAllPictures(); // 获取所有图片
        if (!pictures.isEmpty()) // 有图片
        {
            // 遍历sheet中的图形
            for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren())
            {
                HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor(); // 获取锚点
                if (shape instanceof HSSFPicture) // 是图片
                {
                    HSSFPicture pic = (HSSFPicture) shape;
                    int pictureIndex = pic.getPictureIndex() - 1; // 图片索引
                    HSSFPictureData picData = pictures.get(pictureIndex); // 图片数据
                    String picIndex = anchor.getRow1() + "_" + anchor.getCol1(); // 行_列作为key
                    sheetIndexPicMap.put(picIndex, picData);
                }
            }
            return sheetIndexPicMap;
        }
        else
        {
            return sheetIndexPicMap; // 无图片返回空Map
        }
    }

    // 获取Excel2007（xlsx）格式的图片数据
    public static Map<String, PictureData> getSheetPictures07(XSSFSheet sheet, XSSFWorkbook workbook)
    {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<String, PictureData>();
        // 遍历sheet的关联文档
        for (POIXMLDocumentPart dr : sheet.getRelations())
        {
            if (dr instanceof XSSFDrawing) // 是绘图对象
            {
                XSSFDrawing drawing = (XSSFDrawing) dr;
                List<XSSFShape> shapes = drawing.getShapes(); // 获取所有图形
                for (XSSFShape shape : shapes)
                {
                    if (shape instanceof XSSFPicture) // 是图片
                    {
                        XSSFPicture pic = (XSSFPicture) shape;
                        XSSFClientAnchor anchor = pic.getPreferredSize(); // 获取锚点
                        CTMarker ctMarker = anchor.getFrom(); // 获取起始标记
                        String picIndex = ctMarker.getRow() + "_" + ctMarker.getCol(); // 行_列作为key
                        sheetIndexPicMap.put(picIndex, pic.getPictureData()); // 存入Map
                    }
                }
            }
        }
        return sheetIndexPicMap;
    }

    // 格式化日期对象为字符串（支持Date/LocalDateTime/LocalDate）
    public String parseDateToStr(String dateFormat, Object val)
    {
        if (val == null)
        {
            return "";
        }
        String str;
        if (val instanceof Date) // java.util.Date
        {
            str = DateUtils.parseDateToStr(dateFormat, (Date) val);
        }
        else if (val instanceof LocalDateTime) // LocalDateTime
        {
            str = DateUtils.parseDateToStr(dateFormat, DateUtils.toDate((LocalDateTime) val));
        }
        else if (val instanceof LocalDate) // LocalDate
        {
            str = DateUtils.parseDateToStr(dateFormat, DateUtils.toDate((LocalDate) val));
        }
        else // 其他类型直接toString
        {
            str = val.toString();
        }
        return str;
    }

    // 判断是否有子列表字段
    public boolean isSubList()
    {
        return StringUtils.isNotNull(subFields) && subFields.size() > 0;
    }

    // 判断对象的子列表是否不为空
    public boolean isSubListValue(T vo)
    {
        return StringUtils.isNotNull(subFields) && subFields.size() > 0 && StringUtils.isNotNull(getListCellValue(vo)) && getListCellValue(vo).size() > 0;
    }

    // 通过反射调用getter获取集合属性值
    public Collection<?> getListCellValue(Object obj)
    {
        Object value;
        try
        {
            value = subMethod.invoke(obj, new Object[] {}); // 调用getter方法
        }
        catch (Exception e)
        {
            return new ArrayList<Object>(); // 异常返回空列表
        }
        return (Collection<?>) value;
    }

    // 获取子列表的getter方法
    public Method getSubMethod(String name, Class<?> pojoClass)
    {
        StringBuffer getMethodName = new StringBuffer("get"); // 拼接getter方法名
        getMethodName.append(name.substring(0, 1).toUpperCase()); // 首字母大写
        getMethodName.append(name.substring(1)); // 拼接剩余部分
        Method method = null;
        try
        {
            method = pojoClass.getMethod(getMethodName.toString(), new Class[] {}); // 获取方法
        }
        catch (Exception e)
        {
            log.error("获取对象异常{}", e.getMessage());
        }
        return method;
    }
}