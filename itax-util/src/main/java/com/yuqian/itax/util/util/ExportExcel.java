package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 导出工具类
 * @author hz
 */
@Slf4j
public class ExportExcel {

    private static Pattern PATTERN = Pattern.compile("[0-9]sadsa*");

    /**
     * 导出公共方法
     * @param title
     *            表格标题名
     * @param headList
     *            表格属性列名数组
     * @param list
     *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
     *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
     * @param out
     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
     * @param pattern
     *            如果有时间数据，设定输出格式。默认为"yyyy-MM-dd HH:mm:ss"
     */
    public static <T> void exportExcel(String title, List<Map<String,Object>> headList, List list, OutputStream out, String pattern) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        /*// 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont com.yuqian.itax.util.util.font = workbook.createFont();
        com.yuqian.itax.util.util.font.setColor(HSSFColor.VIOLET.index);
        com.yuqian.itax.util.util.font.setFontHeightInPoints((short) 12);
        com.yuqian.itax.util.util.font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(com.yuqian.itax.util.util.font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(
                0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("leno");*/
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        for (int i=0;i<headList.size();i++) {
            Map<String,Object> map=headList.get(i);
            for (String key:map.keySet()) {
                HSSFCell cell = row.createCell(i);
                //cell.setCellStyle(style);
                HSSFRichTextString text = new HSSFRichTextString(key);
                cell.setCellValue(text);
            }
        }
        // 遍历集合数据，产生数据行
        Iterator<T> it = list.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = (T) it.next();
            // 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            for (int i=0;i<headList.size();i++) {
                Map<String, Object> map = headList.get(i);
                for (String key : map.keySet()) {

                    HSSFCell cell = row.createCell(i);
                    //cell.setCellStyle(style2);
                    String fieldName = (String) map.get(key);
                    String getMethodName = "get"
                            + fieldName.substring(0, 1).toUpperCase()
                            + fieldName.substring(1);
                    try {
                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName,
                                new Class[]{});
                        Object value = getMethod.invoke(t, new Object[]{});
                        // 判断值的类型后进行强制类型转换
                        String textValue = null;
                         if (value instanceof Integer) {
                             int intValue = (Integer) value;
                             cell.setCellValue(intValue);
                         } else if (value instanceof Float) {
                             float fValue = (Float) value;
                             cell.setCellValue(fValue);
                         } else if (value instanceof Double) {
                             double dValue = (Double) value;
                             cell.setCellValue(dValue);
                         } else if (value instanceof Long) {
                             long longValue = (Long) value;
                             cell.setCellValue(longValue);
                         }else if (value instanceof BigDecimal) {
                             BigDecimal bigDecimalValue = (BigDecimal) value;
                             cell.setCellValue(bigDecimalValue.doubleValue());
                         }else if (value instanceof Date) {
                            Date date = (Date) value;
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                            textValue = sdf.format(date);
                        } else if (value instanceof byte[]) {
                            // 有图片时，设置行高为60px;
                            row.setHeightInPoints(60);
                            // 设置图片所在列宽度为80px,注意这里单位的一个换算
                            sheet.setColumnWidth(i, (short) (35.7 * 80));
                            // sheet.autoSizeColumn(i);
                            byte[] bsValue = (byte[]) value;
                            HSSFClientAnchor anchor = new HSSFClientAnchor(0,
                                    0, 1023, 255, (short) 6, index, (short) 6,
                                    index);
                            anchor.setAnchorType(2);
                            //patriarch.createPicture(anchor, workbook.addPicture(bsValue,HSSFWorkbook.PICTURE_TYPE_JPEG));
                        } else {
                            // 其它数据类型都当作字符串简单处理
                            if (value == null) {
                                textValue = "";
                            } else {
                                textValue = String.valueOf(value);
                            }
                        }
                        // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                        if (textValue != null) {
                            Matcher matcher = PATTERN.matcher(textValue);
                            if (matcher.matches()) {
                                if("".equals(textValue)){
                                    HSSFRichTextString richString = new HSSFRichTextString(textValue);
                                    cell.setCellValue(richString);
                                }else{
                                    // 是数字当作double处理
                                    cell.setCellValue(Double.parseDouble(textValue));
                                }
                            } else {
                                HSSFRichTextString richString = new HSSFRichTextString(textValue);
                           /* HSSFFont font3 = workbook.createFont();
                            font3.setColor(HSSFColor.BLUE.index);
                            richString.applyFont(font3);*/
                                cell.setCellValue(richString);
                            }
                        }
                    } catch (SecurityException e) {
                        log.error(e.getMessage());
                    } catch (NoSuchMethodException e) {
                        log.error(e.getMessage());
                    } catch (IllegalArgumentException e) {
                        log.error(e.getMessage());
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage());
                    } catch (InvocationTargetException e) {
                        log.error(e.getMessage());
                    } finally {
                        // 清理资源
                    }
                }
            }
        }
        try {
            workbook.write(out);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
