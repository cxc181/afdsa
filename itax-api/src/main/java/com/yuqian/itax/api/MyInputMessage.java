package com.yuqian.itax.api;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;

/**
 * 获取请求参数
 */
public class MyInputMessage implements HttpInputMessage {
    private InputStream body;
    private HttpHeaders headers;
    private boolean isNull;
    private String paramsString;

    public MyInputMessage(HttpInputMessage inputMessage) throws Exception {
        this.paramsString = IOUtils.toString(inputMessage.getBody(), "UTF-8");
        this.headers = inputMessage.getHeaders();
        this.body = IOUtils.toInputStream(this.paramsString, "UTF-8");
        this.isNull = false;

    }

    @Override
    public InputStream getBody() {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getParamsString() {
        return this.paramsString;
    }

    public Boolean isNull() {
        return this.isNull;
    }
}
