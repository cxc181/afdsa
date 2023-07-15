package com.yuqian.itax.yishui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Getter
@Setter
public class YiShuiBaseResp implements Serializable {

    /**
     * 结果未知
     */
    public static final String UNKNOWN_ERR = "9999";
    /**
     * 通讯成功
     */
    public static final String SUCCESS = "200";

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回业务数据
     */
    private String data;
}
