// 包声明
package com.debuff.common.utils.poi;

// 导入POI的单元格和工作簿类
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

// Excel数据格式处理适配器接口，用于自定义单元格格式化
public interface ExcelHandlerAdapter
{
    /**
     * 格式化
     * 
     * @param value 单元格数据值
     * @param args excel注解args参数组
     * @param cell 单元格对象
     * @param wb 工作簿对象
     *
     * @return 处理后的值
     */
    Object format(Object value, String[] args, Cell cell, Workbook wb); // 格式化方法
}