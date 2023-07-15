package com.yuqian.itax.yishui.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Getter
@Setter
public class BankInfoDto implements Serializable {

    /**
     * 人员ID
     */
    private Long professional_id;

    /**
     * 银行卡ID
     */
    private Long professional_bank_id;
    /**
     * 银行卡号，有掩码*
     */
    private String bank_code;

    /**
     * 卡类型
     */
    private Integer bank_type;

}
