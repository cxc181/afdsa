package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class QuerySignResultReq implements Serializable {

    /**
     * 电子牵链接凭据
     */
    private String transactionCode;

}
