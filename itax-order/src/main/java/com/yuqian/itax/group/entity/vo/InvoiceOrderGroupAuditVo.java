package com.yuqian.itax.group.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
public class InvoiceOrderGroupAuditVo implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 集团公司名称
     */
    private String oemCompanyName;

    /**
     * 开票类型名称
     */
    private String invoiceTypeName;

    /**
     * 开票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 增值税费
     */
    private BigDecimal vatFee;

    /**
     * 所得税
     */
    private BigDecimal personalIncomeTax;

    /**
     * 附加税
     */
    private BigDecimal surcharge;

    /**
     * 增值税费 +所得税 + 附加税
     */
    private BigDecimal total;

    /**
     * 状态
     */
    private Integer orderStatus;
}
