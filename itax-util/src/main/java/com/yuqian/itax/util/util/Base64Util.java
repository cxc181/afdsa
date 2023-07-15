package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.Base64;

/**
 * Base64 工具类
 */
@Slf4j
public class Base64Util {
    private static final char LAST2BYTE = (char) Integer.parseInt("00000011", 2);
    private static final char LAST4BYTE = (char) Integer.parseInt("00001111", 2);
    private static final char LAST6BYTE = (char) Integer.parseInt("00111111", 2);
    private static final char LEAD6BYTE = (char) Integer.parseInt("11111100", 2);
    private static final char LEAD4BYTE = (char) Integer.parseInt("11110000", 2);
    private static final char LEAD2BYTE = (char) Integer.parseInt("11000000", 2);
    private static final char[] ENCODETABLE = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    public Base64Util() {
        super();
    }

    public static String encode(byte[] from) {
        StringBuilder to = new StringBuilder((int) ((double) from.length * 1.34D) + 3);
        int num = 0;
        char currentByte = 0;

        int i;
        for (i = 0; i < from.length; ++i) {
            for (num %= 8; num < 8; num += 6) {
                switch (num) {
                    case 0:
                        currentByte = (char) (from[i] & LEAD6BYTE);
                        currentByte = (char) (currentByte >>> 2);
                        break;
                    case 1:
                    case 2:
                        currentByte = (char) (from[i] & LAST6BYTE);
                        break;
                    case 3:
                    case 4:
                        currentByte = (char) (from[i] & LAST4BYTE);
                        currentByte = (char) (currentByte << 2);
                        if (i + 1 < from.length) {
                            currentByte = (char) (currentByte | (from[i + 1] & LEAD2BYTE) >>> 6);
                        }
                        break;
                    case 5:
                    case 6:
                        currentByte = (char) (from[i] & LAST2BYTE);
                        currentByte = (char) (currentByte << 4);
                        if (i + 1 < from.length) {
                            currentByte = (char) (currentByte | (from[i + 1] & LEAD4BYTE) >>> 4);
                        }
                        break;
                    default:
                        break;
                }

                to.append(ENCODETABLE[currentByte]);
            }
        }

        if (to.length() % 4 != 0) {
            for (i = 4 - to.length() % 4; i > 0; --i) {
                to.append("=");
            }
        }

        return to.toString();
    }

    //base64字符串转化成图片
    public static boolean GenerateImage(String imgStr) throws IOException
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null){//图像数据为空
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out=null;
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "d://222.jpg";//新生成的图片
             out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }finally {
            if(out!=null){
                out.close();
            }
        }
    }

    public void base64ToIo(String strBase64) throws IOException {
        String fileName = "d:/gril2.gif"; //生成的新文件
        FileOutputStream out=null;
        ByteArrayInputStream in=null;
        try {
            // 解码，然后将字节转换为文件
            byte[] bytes = new BASE64Decoder().decodeBuffer(strBase64);   //将字符串转换为byte数组
            in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(fileName);
            int bytesum = 0;
            int byteread = 0;
            in.close();
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); //文件写操作
            }
        } catch (IOException ioe) {
           log.error(ioe.getMessage());
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                   log.error(e.getMessage());
                }
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 本地图片转化成Base64字符串
     * @param localImgPath
     * @return Base64字符串
     */
    public static String getLocalImageBase64(String localImgPath) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = localImgPath;// 待处理的图片
        InputStream in = null;
        byte[] data = null;
        String encode = null; // 返回Base64编码过的字节数组字符串
        // 对字节数组Base64编码
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            // 读取图片字节数组
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            // encode = encoder.encode(data);
            encode = encoder.encodeToString(data);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return encode;
    }
}
