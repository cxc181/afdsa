/*
 * Copyright (C) 2012-2017 zhuoyue soft Co., Ltd
 * All copyrights reserved by zhuoyue.
 * Any copying, transferring or any other usage is prohibited.
 * Or else, zhuoyue possesses the right to require legal
 * responsibilities from the violator.
 * All third-party contributions are distributed under license by
 * zhuoyue soft Co., Ltd.
 */
package com.yuqian.itax.common.httpclient.vo;

/**
 * @author tiny
 * @date 2018年1月4日 下午11:32:04
 * @since JDK 1.8
 */
public class HttpClientResultVo {

    private Integer code;

    private String body;

    public HttpClientResultVo(Integer code, String body) {
        super();
        this.code = code;
        this.body = body;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
