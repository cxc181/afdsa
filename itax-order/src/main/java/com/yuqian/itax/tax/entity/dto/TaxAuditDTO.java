package com.yuqian.itax.tax.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class TaxAuditDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 企业税单id
     */
    @NotNull(message = "企业税单id不能为空")
    private Long companyTaxBillId;

    /**
     * 审核结果 1-通过 2-不通过
     */
    @NotNull(message = "审核结果不能为空")
    private Integer auditResult;

    /**
     * 应退税费
     */
    private Long recoverableTaxMoney;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人
     */
    private String operator;
}
