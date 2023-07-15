package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
@Slf4j
public class ExcelUtils {
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, boolean isCreateHeader, HttpServletResponse response){
        ExportParams exportParams = new ExportParams(title, sheetName);
        exportParams.setCreateHeadRows(isCreateHeader);
        defaultExport(list, pojoClass, fileName, response, exportParams);

    }
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass,String fileName, HttpServletResponse response){
        defaultExport(list, pojoClass, fileName, response, new ExportParams(title, sheetName));
    }
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response){
        defaultExport(list, fileName, response);
    }

    private static void defaultExport(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response, ExportParams exportParams) {
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        if(workbook != null){
            downLoadExcel(fileName, response, workbook);
        }
    }

    private static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
//            throw new NormalException(e.getMessage());
        }
    }
    private static void defaultExport(List<Map<String, Object>> list, String fileName, HttpServletResponse response) {
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        if(workbook != null){
        	downLoadExcel(fileName, response, workbook);
        }
    }

    public static <T> List<T> importExcel(String filePath,Integer titleRows,Integer headerRows, Class<T> pojoClass){
        if (StringUtils.isBlank(filePath)){
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        }catch (NoSuchElementException e){
//            throw new NormalException("模板不能为空");
        } catch (Exception e) {
            log.error(e.getMessage());
//            throw new NormalException(e.getMessage());
        }
        return list;
    }


    /**
     * 处理excel文件过大分sheet，并且下载
     * @param list 导入集合
     * @param clazz 导入集合类型
     * @param response
     * @param title 文件名，不带后缀
     * @param <T>
     * @throws Exception
     */
//    public static <T> void getExcelAndDown(List<T> list, Class clazz, HttpServletResponse response, String title) throws Exception {
//        Workbook workbook = null;
//        ExportParams params = new ExportParams();
//        workbook = ExcelExportUtil.exportExcel(params, clazz, list);
//        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(title, "utf-8"));
//        workbook.write(response.getOutputStream());
//        workbook.close();
//    }

    /**
     * 读取excel
     * @param filePath
     * @return
     */
    public static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(null == filePath){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else{
                return wb = new XSSFWorkbook(is);
            }
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }finally{
            if(is!=null){
                try {
                    is.close();
                }catch (IOException ex){
                    log.error(ex.getMessage());
                }
            }
        }
        return wb;
    }
}
