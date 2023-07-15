package com.yuqian.itax.corporateaccount.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class CorpAccUnpaidContOrderVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 对公户id
     */
    private Long corporateAccountId;
}
