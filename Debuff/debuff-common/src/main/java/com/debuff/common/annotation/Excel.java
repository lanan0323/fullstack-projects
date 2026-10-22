// 包声明：Excel导出注解所在包
package com.debuff.common.annotation;

// 导入Java注解相关类
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
// 导入BigDecimal用于精度控制
import java.math.BigDecimal;
// 导入POI水平对齐方式
import org.apache.poi.ss.usermodel.HorizontalAlignment;
// 导入POI索引颜色枚举
import org.apache.poi.ss.usermodel.IndexedColors;
// 导入自定义Excel处理器适配器
import com.debuff.common.utils.poi.ExcelHandlerAdapter;

/**
 * 自定义导出Excel数据注解
 * 
 * @author ruoyi
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Target(ElementType.FIELD) // 注解作用于字段上
public @interface Excel
{
    /**
     * 导出时在excel中排序
     */
    public int sort() default Integer.MAX_VALUE; // 列排序值，值越小越靠前

    /**
     * 导出到Excel中的名字.
     */
    public String name() default ""; // 列名，即Excel表头显示的名称

    /**
     * 日期格式, 如: yyyy-MM-dd
     */
    public String dateFormat() default ""; // 日期格式化字符串，默认为空

    /**
     * 如果是字典类型，请设置字典的type值 (如: sys_user_sex)
     */
    public String dictType() default ""; // 字典类型，用于将编码转为中文显示

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    public String readConverterExp() default ""; // 读取时的值转换表达式

    /**
     * 分隔符，读取字符串组内容
     */
    public String separator() default ","; // 分隔符，用于拆分多值字符串

    /**
     * BigDecimal 精度 默认:-1(默认不开启BigDecimal格式化)
     */
    public int scale() default -1; // 小数精度位数，-1表示不启用

    /**
     * BigDecimal 舍入规则 默认:BigDecimal.ROUND_HALF_EVEN
     */
    public int roundingMode() default BigDecimal.ROUND_HALF_EVEN; // 舍入模式，默认银行家舍入

    /**
     * 导出时在excel中每个列的高度
     */
    public double height() default 14; // 列高度，默认14

    /**
     * 导出时在excel中每个列的宽度
     */
    public double width() default 16; // 列宽度，默认16

    /**
     * 文字后缀,如% 90 变成90%
     */
    public String suffix() default ""; // 值后缀，如百分号

    /**
     * 当值为空时,字段的默认值
     */
    public String defaultValue() default ""; // 空值时的默认显示值

    /**
     * 提示信息
     */
    public String prompt() default ""; // Excel单元格提示信息

    /**
     * 设置只能选择不能输入的列内容.
     */
    public String[] combo() default {}; // 下拉选项值列表，设置后该列只能选择

    /**
     * 是否需要纵向合并单元格,应对需求:含有list集合单元格)
     */
    public boolean needMerge() default false; // 是否纵向合并相同值的单元格

    /**
     * 是否导出数据,应对需求:有时我们需要导出一份模板,这是标题需要但内容需要用户手工填写.
     */
    public boolean isExport() default true; // 是否导出数据，false时仅导出模板表头

    /**
     * 另一个类中的属性名称,支持多级获取,以小数点隔开
     */
    public String targetAttr() default ""; // 关联目标属性，支持点号分隔的多级属性

    /**
     * 是否自动统计数据,在最后追加一行统计数据总和
     */
    public boolean isStatistics() default false; // 是否启用统计汇总行

    /**
     * 导出类型（0数字 1字符串 2图片）
     */
    public ColumnType cellType() default ColumnType.STRING; // 单元格数据类型，默认字符串

    /**
     * 导出列头背景颜色
     */
    public IndexedColors headerBackgroundColor() default IndexedColors.GREY_50_PERCENT; // 表头背景颜色，默认灰色

    /**
     * 导出列头字体颜色
     */
    public IndexedColors headerColor() default IndexedColors.WHITE; // 表头字体颜色，默认白色

    /**
     * 导出单元格背景颜色
     */
    public IndexedColors backgroundColor() default IndexedColors.WHITE; // 单元格背景颜色，默认白色

    /**
     * 导出单元格字体颜色
     */
    public IndexedColors color() default IndexedColors.BLACK; // 单元格字体颜色，默认黑色

    /**
     * 导出字段对齐方式
     */
    public HorizontalAlignment align() default HorizontalAlignment.CENTER; // 对齐方式，默认居中

    /**
     * 自定义数据处理器
     */
    public Class<?> handler() default ExcelHandlerAdapter.class; // 自定义数据处理器类

    /**
     * 自定义数据处理器参数
     */
    public String[] args() default {}; // 传递给自定义处理器的参数

    /**
     * 字段类型（0：导出导入；1：仅导出；2：仅导入）
     */
    Type type() default Type.ALL; // 字段操作类型，默认导出导入都支持

    // 字段操作类型枚举
    public enum Type
    {
        ALL(0), EXPORT(1), IMPORT(2); // 全部/仅导出/仅导入
        private final int value; // 枚举值对应的数字

        Type(int value) // 构造方法
        {
            this.value = value; // 赋值枚举数字
        }

        public int value() // 获取枚举数字值
        {
            return this.value; // 返回数字
        }
    }

    // 单元格数据类型枚举
    public enum ColumnType
    {
        NUMERIC(0), STRING(1), IMAGE(2); // 数字/字符串/图片
        private final int value; // 枚举值对应的数字

        ColumnType(int value) // 构造方法
        {
            this.value = value; // 赋值枚举数字
        }

        public int value() // 获取枚举数字值
        {
            return this.value; // 返回数字
        }
    }
}
