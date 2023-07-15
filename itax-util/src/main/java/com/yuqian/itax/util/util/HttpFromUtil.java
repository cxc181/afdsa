package com.yuqian.itax.util.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * className HttpFromUtil
 * Description
 * author Jerry
 * DATE 2019/3/7 16:43
 * version [版本号,  2017/6/5]
 */
public class HttpFromUtil {

    public static String http(String url, Map<String, String> params) throws IOException {
        URL u = null;
        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        // 读取返回内容
        StringBuilder buffer = new StringBuilder();
        OutputStreamWriter osw =null;
        if (params != null) {
            params.forEach((key, value) -> {
                sb.append(key);
                sb.append("=");
                sb.append(value);
                sb.append("&");
            });
            sb.substring(0, sb.length() - 1);
        }
        System.out.println("send_url:" + url);
        System.out.println("send_data:" + sb.toString());
        // 尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            if(null == con){
                throw  new Exception("连接失败");
            }
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);

            //// POST 只能为大写，严格限制，post会不识别
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            osw.write(sb.substring(0, sb.length() - 1).replace("+","%2B").toString());
            osw.flush();

            //一定要有返回值，否则无法把请求发送给server端。
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
        } catch (Exception e) {
             e.printStackTrace();
        } finally {
            if (con != null) {
            con.disconnect();
            }
            if(osw!=null){
                osw.close();
            }
        }

        return buffer.toString();
    }
}