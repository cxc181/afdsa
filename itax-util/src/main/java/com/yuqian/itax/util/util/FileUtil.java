package com.yuqian.itax.util.util;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http2.ByteUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.data.redis.util.ByteUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件读取工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 读取文件内容，作为字符串返回
     */
    public static String readFileAsString(String filePath)throws IOException  {
        File file = new File(filePath);
        FileInputStream fis=null;
        StringBuilder sb=null;
        try {
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            if (file.length() > 1024 * 1024 * 1024) {
                throw new IOException("File is too large");
            }
            sb = new StringBuilder((int) (file.length()));
            // 创建字节输入流
            fis = new FileInputStream(filePath);
            // 创建一个长度为10240的Buffer
            byte[] bbuf = new byte[10240];
            // 用于保存实际读取的字节数
            int hasRead = 0;
            while ( (hasRead = fis.read(bbuf)) > 0 ) {
                sb.append(new String(bbuf, 0, hasRead));
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        if(null != sb) {
            return sb.toString();
        }else{
            return "";
        }
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String imageUrl)  {
        InputStream is= null;
        ByteArrayOutputStream bos =null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(imageUrl);
            bos= new ByteArrayOutputStream(url.getFile().length());
            is=url.openStream();
            in = new BufferedInputStream(is);
            short bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len1;
            while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len1);
            }

            byte[] var7 = bos.toByteArray();
            return var7;
        }catch (IOException ex){
            log.error(ex.getMessage());
            return null;
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
                if(is!=null){
                    is.close();
                }
                if(bos!=null){
                    bos.close();
                }
            } catch (IOException var14) {
                log.error(var14.getMessage());
            }
        }
    }

    /**
     * pdf网络地址转png文件base64
     */
    public static String pdfUrl2pngBase64(String pdfurl) {
        String url="";
        try {
            //下载
            URL urlfile = new URL(pdfurl);
            PDDocument doc = PDDocument.load(urlfile.openStream());
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            String base64 = null;
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", stream);
                base64 = Base64.encode(stream.toByteArray());
                url ="data:image/png;base64," + base64;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return url;
    }
    /**
     * pdf图片转png文件流
     */
    public static InputStream pdf2png(InputStream  pdfInputStream) {
        InputStream byteInputStream=null;
        try {
            PDDocument doc = PDDocument.load(pdfInputStream);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            if (pageCount > 0) {
                BufferedImage image = renderer.renderImage(0, 2.0f);
                image.flush();
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageOutputStream imOut;
                imOut = ImageIO.createImageOutputStream(stream);
                ImageIO.write(image, "png", imOut);
                byteInputStream = new ByteArrayInputStream(stream.toByteArray());
                byteInputStream.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return byteInputStream;
    }
    /**
     * img网络地址转png文件base64
     */
    public static String imgUrl2pngBase64(String imgurl) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // 创建URL
            URL url = new URL(imgurl);
            byte[] by = new byte[1024];
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // 将内容放到内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            is.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Base64.encode(data.toByteArray());
    }

    public static void main(String[] args) {
        String base64 = pdfUrl2pngBase64("https://inv.jss.com.cn/group5/M02/01/1F/wKj6zl8edvqITT7nAACAmkCjnYAAAZYuAE4MNAAAICy891.pdf");
        System.out.println(base64);
        System.out.println("=================================");
        base64 = pdfUrl2pngBase64("http://fp.baiwang.com/fp/d?d=A1803C497FFA9B1285D127238CA05560BF9D96E7444E60A1F35FBE20C9B98717");
        System.out.println(base64);
//        String base64 = imgUrl2pngBase64("https://mass.alipay.com/wsdk/img?fileid=A*TuuyRavLU1MAAAAAAAAAAAAAAQAAAQ&bz=am_afts_openhome&zoom=original");
//        System.out.println(base64);
    }
}
