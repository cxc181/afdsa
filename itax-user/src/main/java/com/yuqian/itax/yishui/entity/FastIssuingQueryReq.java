package com.yuqian.itax.yishui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Getter
@Setter
public class FastIssuingQueryReq implements Serializable {

    /**
     * 第三方单号
     */
    private String request_no;
}
