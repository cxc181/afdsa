package com.yuqian.itax.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 关于excel相关校验工具类
 *
 * @date: 2022年10月10日 下午2:48:58
 * @author cxz
 */
@Slf4j
public class ExcelsUtils {


    /**
     * 检查 Excel 文件里面的表头信息
     * @param file
     * @param dataList
     * @return
     */
    public boolean checkExcelHeaders(MultipartFile file, List<String> dataList) throws IOException {
        InputStream inputStream = null;
        try {
            byte [] byteArr=file.getBytes();
            inputStream = new ByteArrayInputStream(byteArr);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(sheet.getLastRowNum());
            //获取 excel 第一行数据（表头）
            Row row = sheet.getRow(0);
            //存放表头信息
            List<String> headerList = new ArrayList<>();
            //算下有多少列
            int colCount = sheet.getRow(0).getLastCellNum();
            // 这里的5默认第一行为5列
            for (int j = 0; j <5; j++) {
                Cell cell = row.getCell(j);
                String cellValue = cell.getStringCellValue().trim();
                System.out.println("返回的值:"+cellValue);
                if(StringUtils.isNotBlank(cellValue)) {
                    headerList.add(cellValue);
                }
            }
            if(headerList.size() != dataList.size()){
                return false;
            }else{
                Collections.sort(dataList);
                Collections.sort(headerList);
                for(int i=0;i<dataList.size();i++){
                    if(!dataList.get(i).equals(headerList.get(i)))
                        return false;
                }
                return true;
            }
            //return dataList.equals(headerList);
        }catch (Exception e){
            log.error("模版表头解析错误：", e);
            return false;
        }finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
    }



}
