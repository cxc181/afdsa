package com.yuqian.itax.util.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class PDFUtil {

	/**
	 *  PDF模板生成PDF文件
	 * @param templateBytes - 模板文件字节对象
	 * @param paramsObj - 模板文件参数对应对象
	 * @param out - 输出流，请自行关闭，已flush
	 */
	public static void pdfFill(byte[] templateBytes,Object paramsObj,OutputStream out){
		PdfReader reader;
		ByteArrayOutputStream bos;
		PdfStamper stamper;
		try {
			reader = new PdfReader(templateBytes);
			bos = new ByteArrayOutputStream();
			stamper = new PdfStamper(reader, bos);
			AcroFields form = stamper.getAcroFields();
			BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			form.addSubstitutionFont(font);
			java.util.Iterator<String> it = form.getFields().keySet().iterator();
			while(it.hasNext()) {
				String name = it.next().toString();
				System.out.println(name);
				form.setFieldProperty(name, "textfont", font, null);
				Object fieldVal = ReflectUtil.getFieldValue(paramsObj, name);
				form.setField(name, String.valueOf(fieldVal));
			}
			// 如果为false那么生成的PDF文件还能编辑，一定要设为true
			stamper.setFormFlattening(true);
			stamper.close();
//			//数据写出
			byte[] bytes = bos.toByteArray();
			out.write(bos.toByteArray(),0,bytes.length);
			bos.flush();
			out.flush();
			bos.close();
		} catch (IOException e) {
			log.error("PDF模板生成文件IO异常",e);
		} catch (DocumentException e) {
			log.error("PDF模板生成文件Document异常",e);
		}
	}

	public static void pdfFill(byte[] templateBytes, JSONObject json, String targetPath) {
		PdfReader reader;
		PdfStamper stamper;
		try {
			reader = new PdfReader(templateBytes);
			stamper = new PdfStamper(reader, new FileOutputStream(targetPath));
			AcroFields form = stamper.getAcroFields();
			BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			form.addSubstitutionFont(font);
			java.util.Iterator<String> it = form.getFields().keySet().iterator();
			while(it.hasNext()) {
				String name = it.next();
//				System.out.println(name);
				JSONObject jsonObject = json.getJSONObject(name);
				if (jsonObject == null) {
					continue;
				}
				if ("1".equals(jsonObject.getString("type"))) {
					form.setFieldProperty(name, "textfont", font, null);
					form.setField(name, jsonObject.getString("name"));
				} else if ("2".equals(jsonObject.getString("type"))) {
					picFill(stamper, form, name, jsonObject.getString("name"));
				}
			}

			// 如果为false那么生成的PDF文件还能编辑，一定要设为true
			stamper.setFormFlattening(true);
			stamper.close();
			reader.close();
		} catch (IOException e) {
			log.error("PDF模板生成文件IO异常",e);
		} catch (DocumentException e) {
			log.error("PDF模板生成文件Document异常",e);
		}
	}

	/**
	 * pdf图片填充
	 * @param stamper
	 * @param form
	 * @param fieldName
	 * @param imagePath
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void picFill(PdfStamper stamper, AcroFields form, String fieldName, String imagePath) throws IOException, DocumentException {

		// 通过域名获取所在页和坐标，左下角为起点
		List<AcroFields.FieldPosition> fieldPositions = form.getFieldPositions(fieldName);
		if (CollectionUtil.isEmpty(fieldPositions)) {
			return;
		}
		// 读图片
		Image image = Image.getInstance(imagePath);
		for (AcroFields.FieldPosition fieldPosition : fieldPositions) {
			int pageNo = fieldPosition.page;
			Rectangle signRect = fieldPosition.position;
			float x = signRect.getLeft();
			float y = signRect.getBottom();

			// 获取操作的页面
			PdfContentByte under = stamper.getOverContent(pageNo);
			// 根据域的大小缩放图片
			image.scaleToFit(signRect.getWidth(), signRect.getHeight());
			// 添加图片
			image.setAbsolutePosition(x, y);
			under.addImage(image);
		}
	}

	/**
	 * 获取pdfjson
	 * @param name 字段值
	 * @param type 字段类型，1：文字，2：图片
	 * @return
	 */
	public static JSONObject getJson(String name, String type) {
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("type", type);
		return json;
	}

	/**
	 * 根据html 获取pdf
	 * @param htmlUrl
	 * @param jsonObject
	 * @param outputDir
	 * @param agreementName
	 */
	public static void getPdfByHtmlContent(String htmlUrl,JSONObject jsonObject,String outputDir,String agreementName){
		ConverterProperties props = new ConverterProperties();
		FontProvider fp = new FontProvider();
		fp.addStandardPdfFonts();
		String resource = PDFUtil.class.getResource("/").getPath();
		System.out.print("===========文件路径："+resource);
		fp.addDirectory(resource+ "fonts");
		props.setFontProvider(fp);
		Document document  = null;
		com.itextpdf.kernel.pdf.PdfDocument pdf = null;
		try {
			//获取html的内容
			 String htmlContent = HttpClientUtil.doGet(htmlUrl, null);
			//内容替换
			if(jsonObject!=null){
				for(String key:jsonObject.keySet()){
					if(key == "idCardFront" || key == "idCardBack"
							|| key == "legalIdCardFront" || key == "legalIdCardReverse"
							|| key == "agentIdCardFront" || key == "agentIdCardBack"){
						if(""==jsonObject.getString(key) || null == jsonObject.getString(key)){
							htmlContent = htmlContent.replaceAll("#"+key+"#","");
						}else{
							htmlContent = htmlContent.replaceAll("#"+key+"#","<img src='"+jsonObject.getString(key)+"' width='200px' height='200px'/>");
						}
					}else{
						htmlContent = htmlContent.replaceAll("#"+key+"#",jsonObject.containsKey(key)?jsonObject.getString(key):"");
					}
				}
			}
			com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(outputDir + agreementName + ".pdf");
			pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
			pdf.setDefaultPageSize(new com.itextpdf.kernel.geom.PageSize(595.0F, 842.0F));
			document = HtmlConverter.convertToDocument(htmlContent, pdf, props);
		}catch (Exception e){
			log.error("HTML生成PDF Document异常",e);
		}finally {
			if(document!=null) {
				document.close();
			}
			if(pdf!=null) {
				pdf.close();
			}
		}
	}

	public static void testHtml2Pdf() throws Exception{
		String content = "<!DOCTYPE html>\n" +
				"<html lang='zh-cn'>\n" +
				"<head>\n" +
				"  <meta charset='utf-8'></meta>\n" +
				"  <meta http-equiv='X-UA-Compatible' content='IE=edge'></meta>\n" +
				"  <meta name=\"renderer\" content=\"webkit\"></meta>\n" +
				"  <meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"></meta>\n" +
				"  <title>委托注册服务协议</title>\n" +
				"  <script src=\"https://cdn.staticfile.org/jquery/3.4.1/jquery.min.js\"></script>\n" +
				"  <style>\n" +

				"  </style>\n" +
				"</head><body>\n" +
				"\n" +
				"\n" +
				"<p><span style=\"font-size:22.0pt;font-family:宋体\"></span></p><p>\n" +
				"\n" +
				"\n" +
				"\n" +
				"<p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"12.0pt;mso-ascii-font-family:Calibri;mso-ascii-theme-font:minor-latin;\n" +
				"mso-fareast-font-family:宋体;mso-fareast-theme-font:minor-fareast;mso-hansi-font-family:\n" +
				"Calibri;mso-hansi-theme-font:minor-latin;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"\n" +
				"<p><span style=\"font-size:12.0pt;font-family:宋体\"></span></p>\n" +
				"\n" +
				"<p><span style=\"font-size:12.0pt;font-family:宋体\"></span></p></p><table style=\"width:479.1pt;border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;\n" +
				" mso-yfti-tbllook:1184;mso-padding-alt:0cm 5.4pt 0cm 5.4pt\">\n" +
				" <tbody><tr style=\"mso-yfti-irow:0;mso-yfti-firstrow:yes;height:31.4pt\">\n" +
				"  <td colspan=\"6\" style=\"width:479.1pt;border:solid windowtext 1.0pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:31.4pt\">\n" +
				"  <p style=\"text-align:center\"><b><span style=\"font-size:18.0pt;mso-bidi-font-size:20.0pt;font-family:宋体;mso-ascii-theme-font:\n" +
				"  minor-fareast;mso-fareast-theme-font:minor-fareast;mso-hansi-theme-font:minor-fareast;\n" +
				"  mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:minor-bidi\"=\"\">产品调查问卷</span></b><b><span style=\"font-family:宋体;mso-ascii-theme-font:minor-fareast;\n" +
				"  mso-fareast-theme-font:minor-fareast;mso-hansi-theme-font:minor-fareast;\n" +
				"  mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:minor-bidi\"=\"\"></span></b></p>\n" +
				"  </td>\n" +
				" </tr>\n" +
				" <tr style=\"mso-yfti-irow:1;height:25.9pt\">\n" +
				"  <td style=\"width:56.55pt;border:solid windowtext 1.0pt;border-top:\n" +
				"  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
				"  padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\">医院</span></p>\n" +
				"  </td>\n" +
				"  <td style=\"width:184.7pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\"><p>#userName#</p></td>\n" +
				"  <td style=\"width:35.6pt;border-top:none;border-left:none;border-bottom:\n" +
				"  solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;mso-border-top-alt:\n" +
				"  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
				"  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\">等级</span></p>\n" +
				"  </td>\n" +
				"  <td style=\"width:71.0pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\"></td>\n" +
				"  <td style=\"width:42.65pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\">城市</span></p>\n" +
				"  </td>\n" +
				"  <td style=\"width:88.6pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\"></td>\n" +
				" </tr>\n" +
				" <tr style=\"mso-yfti-irow:2;height:25.9pt\">\n" +
				"  <td style=\"width:56.55pt;border:solid windowtext 1.0pt;border-top:\n" +
				"  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
				"  padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\">医生姓名</span></p>\n" +
				"  </td>\n" +
				"  <td style=\"width:184.7pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\"><p>#userName#</p></td>\n" +
				"  <td style=\"width:35.6pt;border-top:none;border-left:none;border-bottom:\n" +
				"  solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;mso-border-top-alt:\n" +
				"  solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:\n" +
				"  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\">职称</span></p>\n" +
				"  </td>\n" +
				"  <td style=\"width:71.0pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\"></td>\n" +
				"  <td style=\"width:42.65pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\">科室</span></p>\n" +
				"  </td>\n" +
				"  <td style=\"width:88.6pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:25.9pt\"></td>\n" +
				" </tr>\n" +
				" <tr style=\"mso-yfti-irow:3;height:38.75pt\">\n" +
				"  <td style=\"width:56.55pt;border:solid windowtext 1.0pt;border-top:\n" +
				"  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
				"  padding:0cm 5.4pt 0cm 5.4pt;height:38.75pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\">调查问卷</span></p>\n" +
				"  </td>\n" +
				"  <td colspan=\"5\" style=\"width:422.55pt;border-top:none;\n" +
				"  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:38.75pt\">\n" +
				"  <p><span style=\"font-size:12.0pt;font-family:宋体;\n" +
				"  mso-fareast-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  </td>\n" +
				" </tr>\n" +
				" <tr style=\"mso-yfti-irow:4;mso-yfti-lastrow:yes;height:18.4pt\">\n" +
				"  <td style=\"width:56.55pt;border:solid windowtext 1.0pt;border-top:\n" +
				"  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;\n" +
				"  padding:0cm 5.4pt 0cm 5.4pt;height:18.4pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:=\"\" minor-bidi\"=\"\">填表人：</span><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  </td>\n" +
				"  <td colspan=\"2\" style=\"width:220.3pt;border-top:none;border-left:\n" +
				"  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:18.4pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:=\"\" minor-bidi;mso-no-proof:yes\"=\"\">${visitorName}</span><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;font-family:宋体;mso-ascii-theme-font:\n" +
				"  minor-fareast;mso-fareast-theme-font:minor-fareast;mso-hansi-theme-font:minor-fareast;\n" +
				"  mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  </td>\n" +
				"  <td colspan=\"2\" style=\"width:113.65pt;border-top:none;\n" +
				"  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:18.4pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;\n" +
				"  font-family:宋体;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:=\"\" minor-bidi\"=\"\">填表时间：</span><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-ascii-theme-font:minor-fareast;mso-fareast-theme-font:\n" +
				"  minor-fareast;mso-hansi-theme-font:minor-fareast;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";=\"\" mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  </td>\n" +
				"  <td style=\"width:88.6pt;border-top:none;border-left:none;\n" +
				"  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;\n" +
				"  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;\n" +
				"  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:18.4pt\">\n" +
				"  <p><span style=\"font-size:11.0pt;mso-bidi-font-size:\n" +
				"  12.0pt;font-family:宋体;mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:=\"\" minor-bidi;mso-no-proof:yes\"=\"\">${visitorDate}</span><span style=\"font-size:11.0pt;mso-bidi-font-size:12.0pt;font-family:宋体;mso-ascii-theme-font:\n" +
				"  minor-fareast;mso-fareast-theme-font:minor-fareast;mso-hansi-theme-font:minor-fareast;\n" +
				"  mso-bidi-font-family:\" times=\"\" new=\"\" roman\";mso-bidi-theme-font:minor-bidi\"=\"\"></span></p>\n" +
				"  </td>\n" +
				" </tr>\n" +
				"</tbody></table>\n" +
				"</body>\n" +
				"</html>\n";
//		content = content.replaceAll("&nbsp;"," ");
//		File pdfFile = new File("C:\\Users\\jiangni\\Desktop\\text.pdf");
//		OutputStream output = new FileOutputStream(pdfFile);
//		ITextRenderer renderer = new ITextRenderer();
//		renderer.setDocumentFromString(content);
//		renderer.createPDF(output);

//		ConverterProperties props = new ConverterProperties();
//		DefaultFontProvider defaultFontProvider = new DefaultFontProvider(false,false,false);
//		defaultFontProvider.addFont("C:\\Users\\jiangni\\Desktop\\simsun.ttc");
//		props.setFontProvider(defaultFontProvider);

		ConverterProperties props = new ConverterProperties();
		FontProvider fp = new FontProvider();
		fp.addStandardPdfFonts();
		String resource = PDFUtil.class.getResource("/").getPath();
		fp.addDirectory(resource+ "/fonts");
//		fp.addFont(resource+ "/font" +"\\simsun.ttf");
//		fp.addFont(resource+ "/font" +"\\simsun.ttc");
		props.setFontProvider(fp);

		String content1 = HttpClientUtil.doGet("https://oss-itax-public.inabei.cn/page/aaa.html",null);

		com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter("C:\\Users\\jiangni\\Desktop\\text.pdf");
		com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
		pdf.setDefaultPageSize(new com.itextpdf.kernel.geom.PageSize(595.0F,842.0F));
		Document document = HtmlConverter.convertToDocument(content1,pdf,props);
		document.close();
		pdf.close();

	}

	public static void main(String[] args){
/*		JSONObject jsonObject = new JSONObject();
		jsonObject.put("oemName","云财在线");
		jsonObject.put("companyName","湖南国金通汇信息技术有限公司");
		jsonObject.put("customerServiceTel","400-0819-091");
		jsonObject.put("oemStatus","1");

		String content = "{\"legalIdCardReverse\":\"https://oss-itax-private.inabei.cn/YSC/20210901/dd5c6044-e9d1-453a-b4bd-f30fae055f07.png?Expires=1646224528&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=YVqCumeFivVta9t35MFTBpfsTKM%3D\",\"parkName\":\"长沙二号园区（岳麓区）\",\"prodAmount\":\"999.90\",\"parkBelongsCompanyAddress\":\"湖南省长沙市岳麓区观沙岭街道潇湘北路与北津城路交汇处岳华新苑6栋15层\",\"idCardNo\":\"430602198311152530\",\"oemCompanyName\":\"国金汇彩科技有限公司\",\"parkBelongsCompanyName\":\"湖南湘新数字科技有限公司\",\"oemBelongsCompanyAddress\":\"123\",\"signDate\":{\"name\":\"2022年03月02日\",\"type\":\"1\"},\"legalSex\":\"男\",\"operatorName\":\"何兢立\",\"oemBankName\":\"\",\"oemName\":\"税筹云测试\",\"memberPhone\":\"18900000004\",\"oemEin\":\"123\",\"shopNameOne\":\"\",\"businessAddress\":\"湖南省长沙市岳麓区中业云谷大厦12层0764\",\"agentStartTime\":\"2022年03月02日\",\"agentAccount\":\"18207409328\",\"agentIdCardNo\":\"430602198311152530\",\"legalIdCardAddr\":\"随便咯\",\"userSex\":\"男\",\"recipientPhone\":\"17612106685\",\"parkAddress\":\"湖南省长沙市岳麓区\",\"signTime\":\"2022年03月02日\",\"signEndDate\":{\"name\":\"2023年03月02日\",\"type\":\"1\"},\"legalIdCardNumber\":\"430602198311152530\",\"agentName\":\"何兢立\",\"businessScope\":\"机械设备租赁;办公设备租赁服务;租赁服务（不含许可类租赁服务）;计算机及办公设备维修;专用设备修理;通用设备修理\",\"agentEndTime\":\"2023年03月02日\",\"annualFee\":\"100.01\",\"idCardFront\":\"https://oss-itax-private.inabei.cn/YSC/20210901/46cf5f29-488a-4ccd-a385-161164efa388.png?Expires=1646224528&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=H%2FQRfXdX%2FjrAmmnSuSNzzr5ilJk%3D\",\"legalIdCardFront\":\"https://oss-itax-private.inabei.cn/YSC/20210901/46cf5f29-488a-4ccd-a385-161164efa388.png?Expires=1646224528&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=H%2FQRfXdX%2FjrAmmnSuSNzzr5ilJk%3D\",\"agentIdCardFront\":\"https://oss-itax-private.inabei.cn/ADMIN/20200731/1596163749998.jpg?Expires=1646224528&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=K6950NfM%2FUWXRvFKYLwzFjarJJU%3D\",\"agentIdCardBack\":\"https://oss-itax-private.inabei.cn/ADMIN/20200731/1596163749999.jpg?Expires=1646224528&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=VP8JpVT1VUBlkcF3nwp7m1Zmk3k%3D\",\"realName\":\"何兢立\",\"idCardAddr\":\"随便咯\",\"registeredName\":\"长沙乐颜机械服务部\",\"shopNameTwo\":\"\",\"parkEin\":\"91430104MA4QHLBY6P\",\"recipient\":\"郭琳\",\"parkCity\":\"长沙\",\"cancelTotalLimit\":\"400000.00\",\"contactPhone\":\"18900000004\",\"idCardBack\":\"https://oss-itax-private.inabei.cn/YSC/20210901/dd5c6044-e9d1-453a-b4bd-f30fae055f07.png?Expires=1646224528&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=YVqCumeFivVta9t35MFTBpfsTKM%3D\",\"signImg\":\"https://oss-itax-private.inabei.cn/YSC/20220302/1646205385621.png?Expires=1646224528&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=j3KhlpDVB8k395yoo0vVcIc%2FKQ4%3D\"}";
		jsonObject = JSONObject.parseObject(content);
		PDFUtil.getPdfByHtmlContent("https://oss-itax-public.inabei.cn/page/REG0302.html",jsonObject,"C:\\Users\\jiangni\\Desktop\\","委托注册协议");*/
		OssUtil.downloadImgToLocal("https://oss-itax-public.inabei.cn/ADMIN/20220302/9527ed0a-773d-469f-94ce-f9ea14fb10d5.jpg","D:\\abc\\123", null);
	}
}
