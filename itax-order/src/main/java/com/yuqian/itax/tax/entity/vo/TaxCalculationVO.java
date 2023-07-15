package com.yuqian.itax.tax.entity.vo;

import com.yuqian.itax.order.entity.InvoiceOrderEntity;
import com.yuqian.itax.park.entity.TaxPolicyEntity;
import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 税费计算VO
 */
@Setter
@Getter
public class TaxCalculationVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 适用类型 1-企业开票 2-企业税单 3-企业预税单
     */
    private int type;

    /**
     * 订单编号（使用类型为1-企业开票时需要）
     */
    private String orderNo;

    /**
     * 计算类型 0-所有 1-只计算已缴税费以及增值附加税 2-只计算所得税
     */
    private int calculationType;

    /**
     * 季度（定时任务需生成当前季度税单时传参,参数为0无效）
     */
    private int season;

    /**
     * 税期所属年（需要指定时传参）
     */
    private int year;

    /**
     * 开票周期开始时间
     */
    private Date start;

    /**
     * 开票周期结束时间
     */
    private Date end;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 增值税率
     */
    private BigDecimal vatRate;

    /**
     * 纳税人类型 1-小规模纳税人 2-一般纳税人
     */
    private Integer taxpayerType;

    /**
     * 税种类型 1-所得税 2-增值税 3-附加税 4-印花税 5-水利建设基金
     */
    private Integer taxType;

    /**
     * 增值税申报周期开票金额
     */
    private Long periodInvoiceAmount;

    /**
     * 本次应缴增值税
     */
    private Long payableVatFee;

    /**
     * 申报周期应缴增值税
     */
    private Long periodPayableVat;

    /**
     * 申报周期已缴增值税
     */
    private Long paidVatFee;

    /**
     * 本次应缴附加税
     */
    private Long payableSurcharge;

    /**
     * 申报周期应缴附加税
     */
    private Long periodPayableSurcharge;

    /**
     * 申报周期已缴附加税
     */
    private Long paidSurcharge;

    /**
     * 本次应缴所得税
     */
    private Long payableIncomeTax;

    /**
     * 申报周期应缴所得税
     */
    private Long periodPayableIncomeTax;

    /**
     * 申报周期应缴印花税
     */
    private Long stampDutyAmount;

    /**
     * 申报周期应缴水利建设基金
     */
    private Long waterConservancyFundAmount;

    /**
     * 申报周期已缴所得税
     */
    private Long paidIncomeTax;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 企业类型 1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     */
    private Integer companyType;

    /**
     * 企业
     */
    private MemberCompanyEntity company;

    /**
     * 税费政策
     */
    private TaxPolicyEntity taxPolicy;

    /**
     * 开票订单
     */
    private InvoiceOrderEntity invoiceOrder;

    /**
     * 税单
     */
    private CompanyTaxBillEntity companyTaxBillEntity;
}
