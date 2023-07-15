package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Setter
@Getter
public class CompanyCorporateAccountVerificationQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 账户明细ID
     */
    @NotNull(message = "请选择账户明细")
    private Long id;
    /**
     * 本次核销额度
     */
    @NotNull(message = "请输入本次核销额度")
    private Long amount;

    /**
     * 开票订单号
     */
    @NotBlank(message = "请输入本次核销开票订单号")
    private String invoiceOrderNo;
    /**
     * 备注
     */
    private String remark;

}
