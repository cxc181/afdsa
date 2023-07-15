package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class QuerySignResultResp extends YiShuiBaseResp implements Serializable {

    /**
     * 0未完成签署 -1:未查询到数据 1:已完成签署 2:已拒签
     */
    private Integer resultCode;
    /**
     * 查询结果描述
     */
    private String resultDesc;

    /**
     * 协议下载地址
     */
    private String downloadUrl;

    /**
     * 协议预览地址
     */
    private String viewPdfUrl;
}
