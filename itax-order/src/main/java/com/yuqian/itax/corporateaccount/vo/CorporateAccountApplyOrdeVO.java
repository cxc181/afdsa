package com.yuqian.itax.corporateaccount.vo;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class CorporateAccountApplyOrdeVO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    /**
     * 订单号
     */
    @Excel(name = "订单编号")
    private String orderNo;
    /**
     * 申请会员账号
     */
    @Excel(name = "申请会员账号")
    private String memberAccount;
    /**
     * 会员姓名
     */
    @Excel(name = "会员姓名")
    private String memberName;
    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String companyName;
    /**
     * 经营者姓名
     */
    @Excel(name = "经营者姓名")
    private String operatorName;
    /**
     * 经营者手机号
     */
    @Excel(name = "经营者手机号")
    private String operatorTel;
    /**
     * 经营者身份证号码
     */
    private String idCardNumber;
    /**
     * 申请时间
     */
    @Excel(name = "申请时间", replace = {"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date addTime;
    /**
     *付款完成时间
     */
    @Excel(name = "付款完成时间", replace = {"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", width = 20)
    private Date payTime;

    /**
     * 办理费
     */
    @Excel(name = "办理费")
    private BigDecimal handleFee;

    /**
     * 托管费
     */
    @Excel(name = "托管费")
    private BigDecimal escrowFee;

    /**
     * 支付金额
     */
    @Excel(name = "支付金额")
    private BigDecimal payAmount;

    /**
     * 申请银行
     */
    private String applyBankName;

    /**
     * 银行总部名称
     */
    @Excel(name = "申请银行")
    private String headquartersName;
    /**
     * 申请状态 0-待付款,1-等待预约,2-已完成,3-已取消
     */
    @Excel(name = "申请状态", replace = { "待付款_0","等待预约_1","已完成_2","已取消_3" })
    private Integer orderStatus;
    /**
     * 园区ID
     */
    private Long parkId;
    /**
     * 园区名称
     */
    @Excel(name = "园区名称")
    private String parkName;
    /**
     * 园区编码
     */
    private String parkCode;
    /**
     * 所属OEM机构
     */
    @Excel(name = "所属OEM机构")
    private String oemName;

    /**
     * 银行总部编号
     */
    private String headquartersNo;

    public void setIdCardNumber(String idCardNumber){
        if (StringUtils.isNotBlank(idCardNumber)) {
            this.idCardNumber = StringUtils.overlay(idCardNumber, "****", 4, idCardNumber.length() - 4);
        }
    }
}
