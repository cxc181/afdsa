package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * 国金推广订单进度统计实体
 *
 * @Date: 2019年12月06日 10:48:28
 * @author Kaven
 */
@Getter
@Setter
public class GjProgressVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 未注册企业数
     */
    @ApiModelProperty(value = "注册未注册企业数")
    private Integer unRegComCount;

    /**
     * 待提交订单数
     */
    @ApiModelProperty(value = "注册待提交订单数")
    private Integer regToBeSubmitCount;

    /**
     * 审核中订单数
     */
    @ApiModelProperty(value = "注册审核中订单数")
    private Integer regToBeCheckCount;

    /**
     * 核名驳回订单数
     */
    @ApiModelProperty(value = "核名驳回订单数")
    private Integer toDismissCount;

    /**
     * 出证中订单数
     */
    @ApiModelProperty(value = "注册出证中订单数")
    private Integer regToCheckoutCount;

    /**
     * 已完成订单数
     */
    @ApiModelProperty(value = "注册已完成订单数")
    private Integer regFinishedCount;

    /**
     * 待提交订单数
     */
    @ApiModelProperty(value = "待提交订单数")
    private Integer invToBeSubmitCount;

    /**
     * 审核中订单数
     */
    @ApiModelProperty(value = "审核中订单数")
    private Integer invToBeCheckCount;

    /**
     * 待签收订单数
     */
    @ApiModelProperty(value = "待签收订单数")
    private Integer invToReceiptCount;

    /**
     * 已完成订单数
     */
    @ApiModelProperty(value = "已完成订单数")
    private Integer invFinishedCount;


   public GjProgressVO(CompanyRegProgressVO companyRegProgressVO, CompanyInvoiceProgressVO companyInvoiceProgressVO){
       this.unRegComCount=companyRegProgressVO.getUnRegComCount();
       this.regToBeSubmitCount=companyRegProgressVO.getToBeSubmitCount();
       this.regToBeCheckCount=companyRegProgressVO.getToBeCheckCount();
       this.regToCheckoutCount=companyRegProgressVO.getToCheckoutCount();
       this.toDismissCount=companyRegProgressVO.getToDismissCount();
       this.regFinishedCount=companyRegProgressVO.getFinishedCount();
       this.invToBeSubmitCount=companyInvoiceProgressVO.getToBeSubmitCount();
       this.invToBeCheckCount=companyInvoiceProgressVO.getToBeCheckCount();
       this.invToReceiptCount=companyInvoiceProgressVO.getToReceiptCount();
       this.invFinishedCount=companyInvoiceProgressVO.getFinishedCount();
   }
}
