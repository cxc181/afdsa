package com.yuqian.itax.util.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class PDFFileTemplateUtils {

    /**
     * 灌注模板
     *
     * @param templetePath - 模板地址
     * @param paramMap     - 参数，需与模板的隐藏域对应
     * @return
     */
    public static byte[] fill(String templetePath, Map<String, PDFParam> paramMap) throws Exception{
        byte[] templateBytes = FileUtil.readBytes(templetePath);
        return fill(templateBytes, paramMap);
    }

    public static byte[] fill(byte[] templateBytes, Map<String, PDFParam> paramMap) throws Exception {
        PdfReader reader;
        ByteArrayOutputStream out = null;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            out = new ByteArrayOutputStream();// 输出流
            reader = new PdfReader(templateBytes);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();

            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            form.addSubstitutionFont(bfChinese);

            Iterator<String> it = form.getFields().keySet().iterator();
            while (it.hasNext()) {
                String name = it.next().toString();
                form.setFieldProperty(name, "textfont", bfChinese, null);
                if (!paramMap.containsKey(name)) {
                    form.setField(name, "");
                    continue;
                }
                try {
                    PDFParam param = paramMap.get(name);
                    if (param == null) {
                        form.setField(name, "");
                        continue;
                    }
                    if (param.getType() == 0) {//文本
                        form.setField(name, param.getValue());
                        continue;
                    }
                    if (param.getType() == 1) {//图片
                        // 通过域名获取所在页和坐标，左下角为起点
                        int pageNo = form.getFieldPositions(name).get(0).page;
                        Rectangle signRect = form.getFieldPositions(name).get(0).position;
                        float x = signRect.getLeft();
                        float y = signRect.getBottom();
                        // 读图片
                        Image image = param.getImage();
                        // 获取操作的页面
                        PdfContentByte under = stamper.getOverContent(pageNo);
                        // 根据域的大小缩放图片
                        image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                        // 添加图片
                        image.setAbsolutePosition(x, y);
                        under.addImage(image);
                    }
                } catch (Exception e) {
                    form.setField(name, "");
                    log.error("PDF模板处理异常", e);
                }
            }
            // 如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.setFormFlattening(true);
            stamper.close();

            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            int templatePages = reader.getNumberOfPages();
            for (int k = 1; k <= templatePages; k++) {
                PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), k);
                copy.addPage(importPage);
            }
            doc.close();
            out.toByteArray();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("处理pdf文件异常", e);
            throw new Exception("处理PDF文件异常:" + e.getMessage());
        }
    }

    /**
     * Method: creatPdfFile
     * <p>
     * Description: 创建pdf文件 //保存到临时文件夹
     *
     * @param templateBytes
     * @param fileName
     * @param paramMap
     * @return
     */
    public static void creatPdfFile(byte[] templateBytes, String fileName, Map<String, Object> paramMap) throws Exception {
        try (FileOutputStream out = new FileOutputStream(FileUtil.getTmpDirPath() + File.separator + fileName);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            //创建一个Document对象的实例
            Document doc = new Document(PageSize.A4);
            //为该Document创建一个FileOutputStream实例
            PdfCopy copy = new PdfCopy(doc, out);
            //开Document
            doc.open();
            // 读取pdf模板
            PdfReader reader = new PdfReader(templateBytes);
            //填充pdf
            fill(paramMap, bos, reader);
            //根据模板页码，分次添加
            for (int pdfPage = 1; pdfPage <= reader.getNumberOfPages(); pdfPage++) {
                PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), pdfPage);
                copy.addPage(importPage);
            }
            doc.close();
        } catch (Exception e) {
            throw new Exception("处理PDF文件异常:" + e.getMessage());
        }
    }

    /**
     * Method: fill
     * <p>
     * Description: 填充pdf
     *
     * @param paramMap
     * @param bos
     * @param reader
     * @return
     */
    private static void fill(Map<String, Object> paramMap, ByteArrayOutputStream bos, PdfReader reader) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(reader, bos);
        AcroFields form = stamper.getAcroFields();
        //中文字体,解决中文不能显示问题
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        form.addSubstitutionFont(bfChinese);
        Iterator<String> it = form.getFields().keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            form.setFieldProperty(name, "textfont", bfChinese, null);
            //key未设置/或者值为null默认设置空字符串
            if (!paramMap.containsKey(name) || ObjectUtil.isNull(paramMap.get(name))) {
                form.setField(name, "");
                continue;
            }
            if (paramMap.get(name) instanceof String) {
                form.setField(name, (String) paramMap.get(name));
                continue;
            }
            if (paramMap.get(name) instanceof Image) {
                // 读图片
                Image image = (Image) paramMap.get(name);
                // 获取操作的页面
                PdfContentByte under = stamper.getOverContent(form.getFieldPositions(name).get(0).page);
                // 通过域名获取所在页和坐标，左下角为起点
                Rectangle signRect = form.getFieldPositions(name).get(0).position;
                // 根据域的大小缩放图片
                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                image.setAbsolutePosition(signRect.getLeft(), signRect.getBottom());
                // 添加图片
                under.addImage(image);
            }
        }
        // 如果为false那么生成的PDF文件还能编辑，一定要设为true
        stamper.setFormFlattening(true);
        stamper.close();
    }


    /**
     * 自由者协议签约
     *
     * @param templateBytes
     * @return
     */
    public static byte[] signAgreement(byte[] templateBytes, byte[] signOfImage, byte[] yishuiChapter, String phone, String signDate, String taxerName, String taxerIdCard) throws Exception, IOException, BadElementException {
        Map<String, PDFParam> map = new HashMap<>();
        Image signImage = Image.getInstance(signOfImage);
        PDFParam p1 = PDFParam.instanceImg("sign_of_image", signImage);
        map.put(p1.getName(), p1);
        PDFParam p2 = PDFParam.instanceImg("sign_of_image_2", signImage);
        map.put(p2.getName(), p2);
        PDFParam p3 = PDFParam.instanceImg("sign_of_image_3", signImage);
        map.put(p3.getName(), p3);
        PDFParam p4 = PDFParam.instanceImg("sign_of_image_4", signImage);
        map.put(p4.getName(), p4);
        PDFParam p5 = PDFParam.instance("phone", phone);
        map.put(p5.getName(), p5);
        PDFParam p6 = PDFParam.instance("sign_date", signDate);
        map.put(p6.getName(), p6);
        PDFParam p7 = PDFParam.instance("taxer_name", taxerName);
        map.put(p7.getName(), p7);
        PDFParam p8 = PDFParam.instance("taxer_id_card", taxerIdCard);
        map.put(p8.getName(), p8);
        Image yishuiChapterImage = Image.getInstance(yishuiChapter);
        PDFParam p11 = PDFParam.instanceImg("yishui_chapter", yishuiChapterImage);
        map.put(p11.getName(), p11);
        PDFParam p12 = PDFParam.instanceImg("yishui_chapter_2", yishuiChapterImage);
        map.put(p12.getName(), p12);
        return fill(templateBytes, map);
    }

    public static void main(String[] args) throws Exception, IOException, BadElementException {
        File file = new File("D:\\360MoveData\\Users\\Tang\\Desktop\\自由职业者签约协议_2.pdf");
        byte[] bytes = FileUtil.readBytes(file);
        File file2 = new File("C:\\Users\\Tang\\Pictures\\aaa.png");
        byte[] bytes2 = FileUtil.readBytes(file2);
        File file23 = new File("C:\\Users\\Tang\\Pictures\\ys_official_seal.png");
        byte[] bytes3 = FileUtil.readBytes(file23);
        byte[] bytes1 = signAgreement(bytes, bytes2, bytes3, "16677889999", "2020-21-12", "走美滋", "430119100100120099");
        FileUtil.writeBytes(bytes1, "D:\\360MoveData\\Users\\Tang\\Desktop\\自由职业者签约协议_1111111111111111111111111.pdf");
    }

}
