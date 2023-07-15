/*
 * Copyright (C) 2012-2017 zhuoyue soft Co., Ltd
 * All copyrights reserved by zhuoyue.
 * Any copying, transferring or any other usage is prohibited.
 * Or else, zhuoyue possesses the right to require legal
 * responsibilities from the violator.
 * All third-party contributions are distributed under license by
 * zhuoyue soft Co., Ltd.
 */
package com.yuqian.itax.common.httpclient.service;

import com.yuqian.itax.common.httpclient.vo.HttpClientResultVo;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Sincci Xu
 * @description HttpClientService
 * @create 2018-01-11 11:14
 **/
@Service
public class HttpClientService {

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig config;
    
    private static String DEFAULT_CHARSET = "UTF-8";
    
    private static String CONTENT_TYPE = "application/json";

    /**
     * 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    public HttpClientResultVo doGet(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);
        CloseableHttpResponse response = this.httpClient.execute(httpGet);
        return new HttpClientResultVo(response.getStatusLine().getStatusCode(),
                EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));
    }

    /**
     * 带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    public HttpClientResultVo doGet(String url, Map<String, String> params) throws Exception {
        if (null == params) {
            return this.doGet(url);
        } else {
            URIBuilder uriBuilder = new URIBuilder(url);
            params.entrySet().forEach(entry -> {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            });
            return this.doGet(uriBuilder.build().toString());
        }

    }

    /**
     * 带参数的post请求
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public HttpClientResultVo doPost(String url, Map<String, String> params) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        if (params != null) {
            List<NameValuePair> list = new ArrayList<>();
            params.entrySet().forEach(entry -> {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            });
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, DEFAULT_CHARSET);
            httpPost.setEntity(urlEncodedFormEntity);
        }
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        return new HttpClientResultVo(response.getStatusLine().getStatusCode(),
                EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));
    }

    /**
     * 带参数的post请求
     *
     * @param url
     * @param jsonBody
     * @return
     * @throws Exception
     */
    public HttpClientResultVo doPostWithJson(String url, String jsonBody) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        httpPost.addHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE);
        StringEntity se = new StringEntity(jsonBody, DEFAULT_CHARSET);
        se.setContentType(CONTENT_TYPE);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE));
        httpPost.setEntity(se);
        httpPost.addHeader("Content-Type", CONTENT_TYPE);
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        return new HttpClientResultVo(response.getStatusLine().getStatusCode(),
                EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));
    }

    /**
     * 不带参数post请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    public HttpClientResultVo doPost(String url) throws Exception {
        return this.doPost(url, null);
    }

}
