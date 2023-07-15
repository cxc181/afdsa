package com.yuqian.itax.util.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * http 工具类
 */
@Slf4j
public class HttpUtil {

    private HttpUtil(){}

    public static String post(String requestUrl, String accessToken, String params)
            throws Exception {
        String contentType = "application/x-www-form-urlencoded";
        return HttpUtil.post(requestUrl, accessToken, contentType, params);
    }

    public static String post(String requestUrl, String accessToken, String contentType, String params)
            throws Exception {
        String encoding = "UTF-8";
        if (requestUrl.contains("nlp")) {
            encoding = "GBK";
        }
        return HttpUtil.post(requestUrl, accessToken, contentType, params, encoding);
    }

    public static String post(String requestUrl, String accessToken, String contentType, String params, String encoding)
            throws Exception {
        String url = requestUrl + "?access_token=" + accessToken;
        return HttpUtil.postGeneralUrl(url, contentType, params, encoding);
    }

    public static String postGeneralUrl(String generalUrl, String contentType, String params, String encoding)
            throws IOException {
        URL url = new URL(generalUrl);
        // 打开和URL之间的连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        // 设置通用的请求属性
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // 得到请求的输出流对象
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(connection.getOutputStream());
            out.write(params.getBytes(encoding));
            out.flush();
            out.close();
        }catch (IOException e){
            log.error(e.getMessage());
        }finally {
            if(out != null){
                out.close();
            }
        }

        // 建立实际的连接
        connection.connect();
        // 获取所有响应头字段
        Map<String, List<String>> headers = connection.getHeaderFields();
        // 遍历所有的响应头字段
        for (Map.Entry<String,List<String>> entry : headers.entrySet()) {
            System.err.println(entry.getKey() + "--->" + entry.getValue());
        }
        // 定义 BufferedReader输入流来读取URL的响应
        BufferedReader in = null;
        String result = "";
        try {
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), encoding));
            String getLine;
            while ((getLine = in.readLine()) != null) {
                result += getLine;
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }finally {
            if(in != null){
                in.close();
            }
        }
        log.error("result:" + result);
        return result;
    }

    /**
     * @Description 通过request获取JSON请求参数
     * @Author  Kaven
     * @Date   2020/3/31 10:06
     * @Param   HttpServletRequest
     * @Return  JSONObject
     * @Exception  IOException
    */
    public static JSONObject getjsonParamByRequest(BodyReaderHttpServletRequestWrapper request) throws IOException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
            return jsonObject;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * @Description 输入流转字节数组
     * @Author  Kaven
     * @Date   2020/3/31 15:00
     * @Param   InputStream
     * @Return  byte[]
     * @Exception  Exception
    */
    public static byte[] getByteByStream(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = is.read(buffer)) != -1){
            bos.write(buffer,0,len);
        }
        bos.flush();
        return bos.toByteArray();
    }

    public static String httpPost(String url, String data) throws Exception {
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        try {
            postMethod.setRequestEntity(new StringRequestEntity(data, "application/json", "utf-8"));
            System.out.println("HttpUtil.httpPost:" + postMethod.toString());

            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                String response = postMethod.getResponseBodyAsString();
                System.out.println("请求[" + url + "]返回:" + response);
                return response;
            } else {
                throw new Exception("http请求响应状态异常,STATUS:"+status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // 释放连接
            postMethod.releaseConnection();
        }
    }

    public static String httpPost(String url,Map<String, String> paramMap) throws Exception{
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, new Integer(20000));
        String response = "";
        try {
            NameValuePair[] nvps = getNameValuePair(paramMap);
            method.setRequestBody(nvps);
            int status = client.executeMethod(method);

            if (status == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                String curline = "";
                while ((curline = reader.readLine()) != null) {
                    response += curline;
                }
                return response;
            }else{
                throw new Exception("http请求响应状态异常,STATUS:"+status);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            method.releaseConnection();
        }
    }

    private static NameValuePair[] getNameValuePair(Map<String, String> bean) {
        List<NameValuePair> x = new ArrayList<NameValuePair>();
        for (Iterator<String> iterator = bean.keySet().iterator(); iterator.hasNext(); ) {
            String type = (String) iterator.next();
            x.add(new NameValuePair(type, String.valueOf(bean.get(type))));
        }
        Object[] y = x.toArray();
        NameValuePair[] n = new NameValuePair[y.length];
        System.arraycopy(y, 0, n, 0, y.length);
        return n;
    }
}
