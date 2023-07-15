package com.yuqian.itax.util.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author Kaven
 * @Description HttpClient工具类
 * @Date 15:24 2019/8/14
 * @Param
 * @return
 **/
@Slf4j
public abstract class HttpClientUtil {

    private static int conTime = 4000;
    private static int reqConTime = 8000;
    private static int socketTime = 8000;

    /**
     * 默认编码格式
     */
    public static final String DEFAULT_CHARSET = "UTF-8";


    public static String doGet(String url, Map<String, String> params){
        return doGet(url, params, DEFAULT_CHARSET);
    }

    /**
     * HTTP Get 获取内容
     * @param url  请求的url地址 ?之前的地址ParamsSignFilter
     * @param params 请求的参数
     * @param charset    编码格式
     * @return    页面内容
     */
    public static String doGet(String url, Map<String,String> params,String charset){
        return doGet(url, params, charset, null);
    }

    /**
     * HTTP GET请求
     * @param url  请求的url地址 ?之前的地址
     * @param params 请求的参数
     * @param charset    编码格式
     * @return    页面内容
     */
    public static String doGet(String url, Map<String,String> params, String charset, Map<String, String> header){
        Assert.isTrue(StringUtils.isNotBlank(url), "the request can not be null");
        CloseableHttpResponse response = null;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(conTime).setConnectionRequestTimeout(reqConTime).setSocketTimeout(socketTime).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        try {
            if(params != null && !params.isEmpty()){
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for(Map.Entry<String,String> entry : params.entrySet()){
                    String value = entry.getValue();
                    if(value != null){
                        pairs.add(new BasicNameValuePair(entry.getKey(),value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);
            if(null != header && !header.isEmpty()) {
                Set<String> keys = header.keySet();
                keys.forEach(key -> {
                    httpGet.setHeader(key, header.get(key));
                });
            }
            httpGet.setHeader(HttpHeaders.CONNECTION, "close");
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null){
                result = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
            log.error(e.getMessage(), e);
        } finally {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }

        }
        return null;
    }

    /**
     * HTTP POST请求
     * 
     * @param url
     * @param params
     * @param encoding
     * @return
     */
    public static String post(String url, List<NameValuePair> params, String encoding) {
        
        if (StringUtils.isBlank(encoding)) {
            encoding = HTTP.DEFAULT_CONTENT_CHARSET;
        }
        
        String body = null;
        HttpEntity entity = null;
        HttpClient httpClient = null;
        try {
            httpClient=new DefaultHttpClient();
            /*
             * Post请求
             */
            HttpPost httppost = new HttpPost(url);
            /*
             * 设置参数
             */
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, encoding);
            httppost.setEntity(formEntity);
            /*
             * 发送请求
             */
            HttpResponse httpresponse = httpClient.execute(httppost);
            /*
             * 获取返回数据
             */
            entity = httpresponse.getEntity();
            body = EntityUtils.toString(entity,encoding);
        } catch (UnsupportedEncodingException e) {
            log.error("发生异常，异常详情UnsupportedEncodingException：{}", e);
        } catch (ClientProtocolException e) {
            log.error("发生异常，异常详情ClientProtocolException：{}", e);
        } catch (IOException e) {
            log.error("发生异常，异常详情IOException：{}", e);
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                log.error("发生异常，异常详情IOException：{}", e);
            }
            if(httpClient!=null){
                httpClient.getConnectionManager().shutdown();
                ((DefaultHttpClient) httpClient).close();
            }
        }
        return body;
    }

    public static String doPostPictrue(String url, JSONObject paramJson, Integer type) {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("httpclient url不能为空");
        }

        CloseableHttpResponse response = null;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(conTime).setConnectionRequestTimeout(reqConTime).setSocketTimeout(socketTime).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (paramJson != null) {
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(paramJson.toJSONString(), ContentType.APPLICATION_JSON));
            }
            httpPost.setHeader(HttpHeaders.CONNECTION, "close");
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity == null){
                return null;
            }

            String result;
            if (entity.getContentType().getValue().contains("application/json")) {
                result = EntityUtils.toString(entity, "utf-8");
                log.error("获取小程序二维码图片失败，返回结果：" + result);
                EntityUtils.consume(entity);
                return "9999";
            }
            byte[] byteArray = EntityUtils.toByteArray(entity);
            String base64Image = "data:image/png;base64,";
            String base64String = Base64.encodeBase64String(byteArray);
            // 1-获取带data url的base64，2-获取不带data url的base64
            if (type == 1) {
                return base64Image + base64String;
            } else if (type == 2) {
                return base64String;
            } else {
                return base64String;
            }
        } catch (Exception e) {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
            log.error(e.getMessage(), e);
        } finally {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            }catch(Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public static String doPostJson(String url, JSONObject paramJson) {
        if(StringUtils.isBlank(url)) {
            throw new RuntimeException("httpclient url不能为空");
        }

        CloseableHttpResponse response = null;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(conTime).setConnectionRequestTimeout(reqConTime).setSocketTimeout(socketTime).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        try {
            HttpPost httpPost = new HttpPost(url);
            if(paramJson != null){
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(paramJson.toJSONString(), ContentType.APPLICATION_JSON));
            }
            httpPost.setHeader(HttpHeaders.CONNECTION, "close");
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            if (entity == null){
                return null;
            }

            return EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            }catch(Exception e) {
                log.info(e.getMessage());
            }
        }
        return null;
    }
    public static String doPost(String url, Map<String,String> params, String charset){
        if(StringUtils.isBlank(url)) {
            throw new RuntimeException("httpclient url不能为空");
        }
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(conTime).setConnectionRequestTimeout(reqConTime).setSocketTimeout(socketTime).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        try {
            List<NameValuePair> pairs = null;
            if(params != null && !params.isEmpty()){
                pairs = new ArrayList<NameValuePair>(params.size());
                for(Map.Entry<String,String> entry : params.entrySet()){
                    String value = entry.getValue();
                    if(value != null){
                        pairs.add(new BasicNameValuePair(entry.getKey(),value));
                    }
                }
            }
            httpPost = new HttpPost(url);
            if(pairs != null && pairs.size() > 0){
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
            }
            httpPost.setHeader(HttpHeaders.CONNECTION, "close");
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null){
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);

            return result;
        } catch (Exception e) {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
            log.error(e.getMessage(), e);
        }finally {
            try {
                if(null != response) {
                    response.close();
                }
                if(null != httpClient) {
                    httpClient.close();
                }
            }catch(Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public static byte[] urlToByte(String url) throws MalformedURLException {
        URL ur = new URL(url);
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new BufferedInputStream(ur.openStream());
            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            return out.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
       return null;
    }
}

