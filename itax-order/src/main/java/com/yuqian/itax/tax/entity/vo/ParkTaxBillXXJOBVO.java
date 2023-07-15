package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ParkTaxBillXXJOBVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId ;
    /**
     * 已交税费
     */
    private Long alreadyTaxMoney;
    /**
     * 已交税费
     */
    private Long shouldTaxMoney;
    /**
     * 开票金额
     */
    private Long invoiceAmount;
    /**
     * 本期开票企业
     */
    private int companyNumber;
    /**
     * 作废/红冲企业
     */
    private int cancellationCompany;
}
