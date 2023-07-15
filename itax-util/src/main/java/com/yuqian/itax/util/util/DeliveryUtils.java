package com.yuqian.itax.util.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2020/2/12 10:54
 *  @Description: 快递实时查询相关工具类
 */
@Slf4j
public class DeliveryUtils {
    public static void main(String[] args) {
        String key = "jRpyQRbs8288";    //贵司的授权key
        String customer = "EA9BC5471AE196DBCC7D049FF7483349";//贵司的查询公司编号
        String com = "yunda";			//快递公司编码
        String num = "3950055201640";	//快递单号
        String phone = "";				//手机号码后四位
        String from = "";				//出发地
        String to = "";					//目的地
        int resultv2 = 0;				//开启行政规划解析

        String result = DeliveryUtils.synQueryData(key,customer,com, num, phone, from, to, resultv2);
        System.out.println(result);
    }

    /**
     * 实时查询请求地址
     */
    private static final String SYNQUERY_URL = "http://poll.kuaidi100.com/poll/query.do";

    /**
     * 实时查询快递单号
     * @param key           授权key
     * @param customer	    快递单号
     * @param com			实时查询公司编号
     * @param num			快递单号
     * @param phone			手机号
     * @param from			出发地城市
     * @param to			目的地城市
     * @param resultv2		开通区域解析功能：0-关闭；1-开通
     * @return
     */
    public static String synQueryData(String key, String customer,String com, String num, String phone, String from, String to, int resultv2) {
        Map<String,Object> jsonMap = new HashMap<String,Object>();
        jsonMap.put("com",com.trim());
        jsonMap.put("num",num.trim());
        jsonMap.put("phone",phone);
        jsonMap.put("from",from);
        jsonMap.put("to",to);
        jsonMap.put("resultv2",1 == resultv2 ? 1 : 0);

        String param = JSON.toJSONString(jsonMap);

        Map<String, String> params = new HashMap<String, String>();
        params.put("customer", customer);
        String sign = Md5Util.encode(param + key + customer);
        params.put("sign", sign);
        params.put("param", param);

        return post(params);
    }

    /**
     * 发送post请求
     */
    public static String post(Map<String, String> params) {
        StringBuffer response = new StringBuffer("");
        BufferedReader reader = null;
        try {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (builder.length() > 0) {
                    builder.append('&');
                }
                builder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                builder.append('=');
                builder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] bytes = builder.toString().getBytes("UTF-8");

            URL url = new URL(SYNQUERY_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(bytes);

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return response.toString();
    }
}