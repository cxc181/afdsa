package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 图片下载工具类
 */
@Slf4j
public class ImageDownloadUtil {


        /**
         * 下载多张图片的压缩包
         * @param request
         * @param response
         * @param imgUrlStr 多张图片URL地址逗号拼接字符串
         * @param zipName 压缩包名称
         * @throws Exception
         */
        public static void downloadImagesZip(HttpServletRequest request, HttpServletResponse response, String imgUrlStr,String zipName) throws Exception {
            downloadImagesZip(request, response, imgUrlStr.split(","), zipName);
        }

        /**
         * 下载多张图片的压缩包
         * @param request
         * @param response
         * @param imgUrlList 多张图片URL地址list集合
         * @param zipName 压缩包名称
         * @throws Exception
         */
        public static void downloadImagesZip(HttpServletRequest request, HttpServletResponse response, List imgUrlList, String zipName) throws Exception {
//            Integer size = imgUrlList == null ? 10 : imgUrlList.size();
            Integer size = imgUrlList.size();
            downloadImagesZip(request, response, (String[]) imgUrlList.toArray(new String[size]), zipName);
        }

        /**
         * 下载多张图片的压缩包
         * @param request
         * @param response
         * @param imgUrls 多张图片URL地址数组
         * @param zipName 压缩包名称
         * @throws Exception
         */
        public static void downloadImagesZip(HttpServletRequest request, HttpServletResponse response, String[] imgUrls,String zipName) throws Exception {
            // 获取多张图片的二进制
            byte [] data = getImagesByte(imgUrls);

            response.reset();
            response.setHeader("content-Disposition", "attachment; filename=\"" + zipName + ".zip\"");
            response.addHeader("Content-Length", "" + data.length);
            response.setContentType("application/zip; charset=UTF-8");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Access-Control-Allow-Origin", "*");
            IOUtils.write(data, response.getOutputStream());
            IOUtils.closeQuietly(response.getOutputStream());
        }


        /**
         * 获取多张图片的二进制
         * @param imgUrls 多张图片URL地址数组
         * @return
         * @throws Exception
         */
        public static byte[] getImagesByte(String[] imgUrls) throws Exception {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            for (String imgUrl : imgUrls) {
                // 获取图片名称
                String imageName = imgUrl.substring(imgUrl.lastIndexOf("||")+2);

                // 将当前的图片放到zip流中传出去
                downLoadImage(imgUrl, imageName, zipOutputStream);
            }

            IOUtils.closeQuietly(zipOutputStream);
            return outputStream.toByteArray();
        }

        /**
         * 将当前的图片放到zip流中传出去
         * @param imageUrl 图片URL地址
         * @param imageName 图片名称
         * @param zipOutputStream zip输出流
         */
        public static void downLoadImage(String imageUrl,String imageName,ZipOutputStream zipOutputStream) {
            String imgArray[] = {"jpg","png","gif","bmp","jpeg"};
            List suffixList = Arrays.asList(imgArray);
            String suffix = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
            suffix=suffix.split("\\?")[0];

            if(StringUtils.isNotBlank(imageUrl)){
                BufferedInputStream in = null;
                try {
                    //校验读取到文件
                    if (!suffixList.contains(suffix)) {
                        // 文件格式不对
                        throw new Exception("不是图片");
                    }

                    imageName += "." + suffix;
                    imageUrl=imageUrl.substring(0,imageUrl.lastIndexOf("||"));
                    URL url = new URL(imageUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty(
                            "Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
                                    + "application/x-shockwave-flash, application/xaml+xml, "
                                    + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
                                    + "application/x-ms-application, application/vnd.ms-excel, "
                                    + "application/vnd.ms-powerpoint, application/msword, */*");
                    conn.setRequestProperty("Accept-Language", "zh-CN");
                    conn.setRequestProperty("Charset", "UTF-8");

                    InputStream inStream = conn.getInputStream();
                    if(inStream == null) {
                        throw new Exception("获取压缩的数据项失败! 图片名为：" + imageName);
                    }else {
                        in = new BufferedInputStream(inStream);
                    }

                    // 压缩条目不是具体独立的文件，而是压缩包文件列表中的列表项，称为条目，就像索引一样
                    //ZipEntry zipEntry = new ZipEntry("图片/" + imageName);

                    ZipEntry zipEntry = new ZipEntry(imageName);
                    // 定位到该压缩条目位置，开始写入文件到压缩包中
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024 * 5]; // 读写缓冲区
                    int read = 0;
                    while ((read = in.read(bytes)) != -1) {
                        zipOutputStream.write(bytes, 0, read);
                    }

                    IOUtils.closeQuietly(inStream); // 关掉输入流
                    IOUtils.closeQuietly(in); // 关掉缓冲输入流
                    zipOutputStream.closeEntry();
                } catch (Exception e) {
                   log.error(e.getMessage());
                }
            }
        }

}
