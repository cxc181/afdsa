package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/27 9:56
 *  @Description: 图片/视频处理工具类
 */
@Slf4j
public class ImageUtils {
    
    public static String getBase64ByImgUrl(String url){
        String suffix = url.substring(url.lastIndexOf(".") + 1);
        try {
            URL urls = new URL(url);
            ByteArrayOutputStream  baos = new ByteArrayOutputStream();
            Image image = Toolkit.getDefaultToolkit().getImage(urls);
            BufferedImage  biOut = toBufferedImage(image);
            ImageIO.write(biOut, suffix, baos);
            String base64Str = Base64Util.encode(baos.toByteArray());
            return base64Str;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "";
        }
    }

    /**
     * 网络图片转换Base64的方法
     *
     * @param
     */
    public static String netImageToBase64(String netImagePath) {
        String strNetImageToBase64 = null;
        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // 创建URL
            URL url = new URL(netImagePath);
            final byte[] by = new byte[1024];
            // 创建链接
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);

            try {
                InputStream is = conn.getInputStream();
                // 将内容读取内存中
                int len = -1;
                while ((len = is.read(by)) != -1) {
                    data.write(by, 0, len);
                }
                // 对字节数组Base64编码
                BASE64Encoder encoder = new BASE64Encoder();
                strNetImageToBase64 = encoder.encode(data.toByteArray());
                // 关闭流
                is.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return strNetImageToBase64;
        } catch (IOException e) {
           log.error(e.getMessage());
            return "error";
        }
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }
    /**
     * 通过图片的url获取图片的base64字符串
     * @param imgUrl    图片url
     * @return    返回图片base64的字符串
     */
    public static String image2Base64(String imgUrl) {
        URL url = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        HttpURLConnection httpUrl = null;
        try{
            url = new URL(imgUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            httpUrl.getInputStream();
            is = httpUrl.getInputStream();

            outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];

            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=is.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            // 对字节数组Base64编码
            return Base64Util.encode(outStream.toByteArray());
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        finally{
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if(outStream != null){
                try {
                    outStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            if(httpUrl != null){
                httpUrl.disconnect();
            }
        }
        return imgUrl;
    }

    /**
     * @Description 将网络图片编码为base64
     * @Author  Kaven
     * @Date   2019/12/27 10:18
     * @Param  URL
     * @Return String
     * @Exception Exception
    */
    public static String encodeImageToBase64(URL url) throws Exception {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        log.info("图片的路径为:" + url.toString());
        //打开链接
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            byte[] data = outStream.toByteArray();
            //对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encode(data);
//            log.info("网络文件[{}]编码成base64字符串:[{}]" + url.toString()+base64);
            return base64;//返回Base64编码过的字节数组字符串
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new Exception("图片上传失败,请联系客服!");
        }
    }

    /**
     * @Description 获取base64文件大小
     * @Author  Kaven
     * @Date   2020/7/20 11:20
     * @Param imageBase
     * @Return int
     * @Exception
    */
    public static int obtainImgSize(String imageBase) {
        String str = imageBase;
        // 处理头部的东西，注意：逗号也必须去除。
        str = str.replace("data:image/png;base64,","");
        str = str.replace("=", "");
        // 这里计算出来的是字节大小，也就是B
        int size = (str.length() - (str.length() / 8) * 2);
        return size;
    }

    /*public static void main(String[] args) throws Exception {
        String reuslt = encodeImageToBase64(new URL("http://itax-private.oss-cn-hangzhou.aliyuncs.com/YCS/20191227/1577411666833.jpg?Expires=1577413256&OSSAccessKeyId=LTAI4Fv76NsWmiFcRNhCFpe1&Signature=2NyHs5ql2zhAn0lNZU4nnru9Kho%3D"));
        log.info(reuslt);
    }*/
}