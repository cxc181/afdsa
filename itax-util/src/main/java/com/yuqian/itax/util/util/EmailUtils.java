package com.yuqian.itax.util.util;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @ClassName EmailUtils
 * @Description TODO
 * @Author jiangni
 * @Date 2020/12/30
 * @Version 1.0
 */
@Slf4j
public class EmailUtils {

//    private static String account = "jiangni@99366.cn";// 登录账户
//    private static String password = "Jn7890";// 登录密码
//    private static String host = "smtp.exmail.qq.com";// 服务器地址
//    private static String port = "465";// 端口
    private static String protocol = "smtp";// 协议
    //初始化参数
    public static Session initProperties(Map<String,String> params) {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtp.host", params.get("emailHost"));
        properties.setProperty("mail.smtp.port", params.get("port"));
        // 使用smtp身份验证
        properties.put("mail.smtp.auth", "true");
        // 使用SSL,企业邮箱必需 start
        // 开启安全协议
        MailSSLSocketFactory mailSSLSocketFactory = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
        }
        properties.put("mail.smtp.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.socketFactory.port", params.get("port"));
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(params.get("account"), params.get("password"));
            }
        });
        // 使用SSL,企业邮箱必需 end
        return session;
    }

    // @param sender 发件人别名
    // @param subject 邮件主题
    //@param content 邮件内容
    //@param receiverList 接收者列表,多个接收者之间用","隔开
    //@param fileSrc 附件地址
    public static void send(Map<String,String> params,String sender, String subject, String content, String receiverList, String fileSrc) throws Exception{
        Session session = initProperties(params);
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(params.get("account"), sender));// 发件人,可以设置发件人的别名
        // 收件人,多人接收
        InternetAddress[] internetAddressTo = new InternetAddress().parse(receiverList);
        mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressTo);
        // 主题
        mimeMessage.setSubject(subject);
        // 时间
        mimeMessage.setSentDate(new Date());
        // 容器类 附件
        MimeMultipart mimeMultipart = new MimeMultipart();
        // 可以包装文本,图片,附件
        MimeBodyPart bodyPart = new MimeBodyPart();
        // 设置内容
        bodyPart.setContent(content, "text/html; charset=UTF-8");
        mimeMultipart.addBodyPart(bodyPart);
       /* // 添加图片&附件
        bodyPart = new MimeBodyPart();
        bodyPart.attachFile(fileSrc);
        mimeMultipart.addBodyPart(bodyPart);*/
        mimeMessage.setContent(mimeMultipart);

        mimeMessage.saveChanges();
        Transport.send(mimeMessage);
    }



    /**
     * 可上传附件版
     * @param params
    // @param sender 发件人别名
    // @param subject 邮件主题
    //@param content 邮件内容
    //@param receiverList 接收者列表,多个接收者之间用","隔开
    //@param fileSrc 附件地址
     * @throws Exception
     */
    public static void send(Map<String,String> params, String sender, String subject, String content, String receiverList, ByteArrayInputStream inputstream, String fileName) throws Exception{
        Session session = initProperties(params);
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(params.get("account"), sender));// 发件人,可以设置发件人的别名
        // 收件人,多人接收
        InternetAddress[] internetAddressTo = new InternetAddress().parse(receiverList);
        mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressTo);
        // 主题
        mimeMessage.setSubject(subject);
        // 时间
        mimeMessage.setSentDate(new Date());
        // 容器类 附件
        MimeMultipart mimeMultipart = new MimeMultipart();
        // 可以包装文本,图片,附件
        MimeBodyPart bodyPart = new MimeBodyPart();
        // 设置内容
        bodyPart.setContent(content, "text/html; charset=UTF-8");
        // 添加图片&附件
        bodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(inputstream, "application/excel")));
        bodyPart.setFileName(fileName);

        mimeMultipart.addBodyPart(bodyPart);
        mimeMessage.setContent(mimeMultipart);
        mimeMessage.saveChanges();
        Transport.send(mimeMessage);
    }

    /**
     * 发送邮件
     *
     * @param to
     *            邮件收件人地址
     * @param copyEmail
     *            邮件抄送地址
     * @param title
     *            邮件标题
     * @param text
     *            内容
     * @param inputstream
     *            附件流
     */
    public static void sendMsgFileDs(Map<String,String> params, String to, String copyEmail, String title, String text,ByteArrayInputStream inputstream) {
        Session session = assembleSession(params);
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(params.get("account")));
            msg.setSubject(title);
            msg.setRecipients(Message.RecipientType.TO, acceptAddressList(to, copyEmail));
            MimeBodyPart contentPart = (MimeBodyPart) createContent(text, inputstream,title);//参数为正文内容和附件流
//			MimeBodyPart stream = new MimeBodyPart();

//			MimeBodyPart part=(MimeBodyPart) createAttachment("D:/test/1.jpg");//可增加多个附件
            MimeMultipart mime = new MimeMultipart("mixed");
            mime.addBodyPart(contentPart);
//			mime.addBodyPart(part);//可增加多个附件
            msg.setContent(mime);
            Transport.send(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    public static Address[] acceptAddressList(String acceptAddress,String acceptAddressOther) {
        // 创建邮件的接收者地址，并设置到邮件消息中
        Address[] tos = null;
        String[] copyEmail = acceptAddressOther.split(",");
        try {
            if (copyEmail != null) {
                // 为每个邮件接收者创建一个地址
                tos = new InternetAddress[copyEmail.length + 1];
                tos[0] = new InternetAddress(acceptAddress);
                for (int i = 0; i < copyEmail.length; i++) {
                    tos[i + 1] = new InternetAddress(copyEmail[i]);
                }
            } else {
                tos = new InternetAddress[1];
                tos[0] = new InternetAddress(acceptAddress);
            }
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            log.error(e.getMessage());
        }
        return tos;
    }

    public static Session assembleSession(Map<String,String> params) {
        Session session = null;
        Properties props=new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.port",params.get("port"));
        props.setProperty("mail.smtp.host", params.get("emailHost"));//邮件服务器
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            log.error(e1.getMessage());
        }
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.ssl.enable", "true");
        session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(params.get("account"), params.get("password"));
            }
        });
        return session;
    }
    static Part createContent(String content,ByteArrayInputStream inputstream,String fileName){
        MimeBodyPart contentPart=null;
        try {
            contentPart=new MimeBodyPart();
            MimeMultipart contentMultipart=new MimeMultipart("related");
            MimeBodyPart htmlPart=new MimeBodyPart();
            htmlPart.setContent(content, "text/html;charset=gbk");
            contentMultipart.addBodyPart(htmlPart);
            //附件部分
            MimeBodyPart excelBodyPart=new MimeBodyPart();
            DataSource dataSource=new ByteArrayDataSource(inputstream, "application/excel");
            DataHandler dataHandler=new DataHandler(dataSource);
            excelBodyPart.setDataHandler(dataHandler);
            excelBodyPart.setFileName(MimeUtility.encodeText(fileName+".xls"));
//			excelBodyPart.setDataHandler(new DataHandler(fileDs));
//			excelBodyPart.setFileName(fileDs.getName());
//			excelBodyPart.setContentID("excel");
            contentMultipart.addBodyPart(excelBodyPart);
            contentPart.setContent(contentMultipart);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return contentPart;
    }


//
//    public static void main(String[] args)throws Exception{
//        Map<String,String> params = new HashMap<>();
//        params.put("account","jiangni@99366.cn");
//        params.put("password","Jn7890");
//        params.put("emailHost","smtp.exmail.qq.com");
//        params.put("port","465");
//        StringBuffer sb = new StringBuffer();
//        /*sb.append("<p>尊敬的 <label>云财百旺</label> 用户，您好：</p>\n" +
//                "   <p>&nbsp;&nbsp;&nbsp;&nbsp; xxx 为您开具了电子发票，请您查收！</p>\n" +
//                "   <br/>\n" +
//                "   <table style=\"border: 2px #9E9E9E solid;width: 50%;text-align: center;border-collapse: collapse;\">\n" +
//                "\t\t<tr style=\"border-bottom:2px #9E9E9E solid;\">\n" +
//                "\t\t\t<td>订单编号</td>\n" +
//                "\t\t\t<td>SX202012121211</td>\n" +
//                "\t\t</tr>\n" +
//                "\t\t<tr style=\"border-bottom:2px #9E9E9E solid;\">\n" +
//                "\t\t\t<td>开票金额</td>\n" +
//                "\t\t\t<td>50000000元</td>\n" +
//                "\t\t</tr>\n" +
//                "\t\t<tr>\n" +
//                "\t\t\t<td>电子发票下载列表</td>\n" +
//                "\t\t\t<td>&nbsp;</td>\n" +
//                "\t\t</tr>\n" +
//                "\t\t<tr>\n" +
//                "\t\t    <td>&nbsp;</td>\n" +
//                "\t\t\t<td>\n" +
//                "\t\t\t\t<a href=\"https://www-pre.baiwang.com/fp/d?d=70CE3DC3703FB796E4C59B7A5509DBB4835E4A83F18BFB9302560E5291A4D3A1\">电子发票1.pdf</a><br/>\n" +
//                "\t\t\t\t<a href=\"https://www-pre.baiwang.com/fp/d?d=70CE3DC3703FB796E4C59B7A5509DBB4835E4A83F18BFB9302560E5291A4D3A1\">电子发票2.pdf</a><br/>\n" +
//                "\t\t\t\t<a href=\"https://www-pre.baiwang.com/fp/d?d=70CE3DC3703FB796E4C59B7A5509DBB4835E4A83F18BFB9302560E5291A4D3A1\">电子发票3.pdf</a><br/>\n" +
//                "\t\t\t\t<a href=\"https://www-pre.baiwang.com/fp/d?d=70CE3DC3703FB796E4C59B7A5509DBB4835E4A83F18BFB9302560E5291A4D3A1\">电子发票4.pdf</a><br/>\n" +
//                "\t\t\t</td>\n" +
//                "\t\t</tr>\n" +
//                "   </table>");*/
//        sb.append("<p>尊敬的 <label>云财百旺</label> 用户，您好：</p>\n");
//
//        String[] headers = {"col1","col2","col3"};
//        // 声明一个工作薄
//        HSSFWorkbook wb = new HSSFWorkbook();
//        // 生成一个表格
//        HSSFSheet sheet = wb.createSheet();
//        HSSFRow row = sheet.createRow(0);
//        for (int i = 0; i < headers.length; i++) {
//            HSSFCell cell = row.createCell(i);
//            cell.setCellValue(headers[i]);
//        }
//        int rowIndex = 1;
//
//        for(int j=0; j<3; j++){
//            row = sheet.createRow(rowIndex);
//            rowIndex++;
//            HSSFCell cell1 = row.createCell(0);
//            cell1.setCellValue(j);
//            cell1 = row.createCell(1);
//            cell1.setCellValue(Double.valueOf(j+1));
//            cell1 = row.createCell(2);
//            cell1.setCellValue(Double.valueOf(j+2));
//        }
//        for (int i = 0; i < headers.length; i++) {
//            sheet.autoSizeColumn(i);
//        }
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream(1000);
//        wb.write(os);
//
//        ByteArrayInputStream iss = new ByteArrayInputStream(os.toByteArray());
//        os.close();
//        send(params,"","海星会员业绩统计",sb.toString(),"382017078@qq.com",iss,"海星会员业绩统计");
//    }
}
