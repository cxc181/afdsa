package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class CompanyTaxBillXXJOBVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区id
     */
    private Long parkId ;
    /**
     *企业ID
     */
    private Long companyId;
    /**
     * 已交税费
     */
    private Long alreadyTaxMoney;
    /**
     * 开票金额
     */
    private Long invoiceAmount;
    /**
     * 增值税
     */
    private Long vatFee;
    /**
     * 附加税
     */
    private Long surcharge;
    /**
     * 个人所得税
     */
    private Long personalIncomeTax;
    /**
     *增值税专用发票开票金额
     */
    private Long zpInvoiceAmount;
    /**
     * 增值税普通发票开票金额
     */
    private Long ppInvoiceAmount;

}
