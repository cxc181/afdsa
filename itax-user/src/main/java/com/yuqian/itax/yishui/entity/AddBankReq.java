package com.yuqian.itax.yishui.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
public class AddBankReq implements Serializable {

    /**
     * 姓名
     */
    private String name;

    /**
     * 银行卡预留手机号
     */
    private String mobile;

    /**
     * 银行卡号
     */
    private String bank_code;

    /**
     * 自由职业者ID
     */
    private Long professional_id;

    /**
     * 1：银行卡；2：支付宝
     */
    private String bank_type = "1";

}
