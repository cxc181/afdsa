package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OrderVoidInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 机构编码
     */
//    @NotBlank(message = "订单编号不能为空")
    private String oemCode;

    /**
     * 订单编号
     */
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    /**
     * 公司名称
     */
    @NotBlank(message = "公司名称不能为空")
    private String companyName;

    /**
     *开票金额
     */
    @NotNull(message = "开票金额不能为空")
    private Long invoiceAmount;

    /**
     * 发票类型 1-纸质发票 2-电子发票
     */
    @NotNull(message = "发票类型不能为空")
    private Integer invoiceWay;

    /**
     * 出票日期
     */
    private Date confirmInvoiceTime;

    /**
     * 开票类目
     */
    @NotBlank(message = "开票类目不能为空")
    private String  categoryName;

    /**
     * 开票类型  1-增值税普通发票 2-增值税专用发票
     */
    @NotNull(message = "开票类型不能为空")
    private Integer invoiceType;

    /**
     * 增值税率
     */
    private BigDecimal VATFeeRate;

    /**
     * 发票备注
     */
    @Size(max = 100, message = "发票备注不能超过100位字符")
    private String invoiceRemark;

    /**
     * 作废凭证
     */
    @NotBlank(message = "作废凭证不能为空")
    private String cancellationVoucher;

    /**
     * 作废/红冲说明
     */
    @NotBlank(message = "作废/红冲说明不能为空")
    @Size(max = 100, message = "作废/红冲说明不能超过100位字符")
    private String cancellationRemark;

    /**
     * 发票标识 0-正常 1-已作废/红冲 2-作废重开
     */
    @NotNull(message = "发票标识不能为空")
    private Integer invoiceMark;

    /**
     * 企业id
     */
    @NotNull(message = "企业id不能为空")
    private Long companyId;

    /**
     * 开票类目id
     */
    @NotNull(message = "开票类目id不能为空")
    private Long categoryId;

    /**
     * 开票类目类型  1 系统开票类目  2用户自定义开票类目
     */
    private Integer categoryType;

    /**
     * 发票抬头公司名称
     */
    @ApiModelProperty(value = "发票抬头公司名称")
    private String headCompanyName;

    /**
     * 发票抬头公司地址
     */
    @ApiModelProperty(value = "发票抬头公司地址")
    private String companyAddress;

    /**
     * 发票抬头税号
     */
    @ApiModelProperty(value = "发票抬头税号")
    private String ein;

    /**
     * 发票抬头电话号码
     */
    @ApiModelProperty(value = "发票抬头电话号码")
    private String phone;

    /**
     * 发票抬头开户银行
     */
    @ApiModelProperty(value = "发票抬头开户银行")
    private String bankName;

    /**
     * 发票抬头银行账号
     */
    @ApiModelProperty(value = "发票抬头银行账号")
    private String bankNumber;

    /**
     * 纳税人类型  1-小规模纳税人 2-一般纳税人
     */
    @ApiModelProperty(value = "纳税人类型")
    private Integer taxpayerType;


}
