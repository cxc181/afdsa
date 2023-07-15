package com.yuqian.itax.tax.entity.vo;


import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class CompanyTaxBillListVOAdmin  implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 税款所属期年
     */
    private Integer taxBillYear;

    /**
     * 税款所属期-季度
     */
    private Integer taxBillSeasonal;
    /**
     * 税款所属期
     */
    @Excel(name = "税款所属期")
    private String taxBillTime;
    /**
     *园区名称
     */
    @Excel(name = "园区名称")
    private String parkName;
    /**
     * 公司名称
     */
    @Excel(name = "公司名称")
    private String companyName;

    /**
     * 经营者名称
     */
    @Excel(name = "经营者名称")
    private String operatorName;
    /**
     * 税号
     */
    @Excel(name = "税号")
    private String ein;
    /**
     * 注册账号
     */
    @Excel(name = "注册账号")
    private String memberAccount;
    /**
     * 用户名称
     */
    @Excel(name = "用户名称")
    private String realName;

    /**
     * 本期开票金额
     */
    @Excel(name = "本期开票金额")
    private BigDecimal invoiceMoney;

    /**
     * 作废/红冲金额
     */
    /*@Excel(name = "作废/红冲金额")*/
    private BigDecimal cancellationAmount;

    /**
     * 已交税费（已缴税费）
     */
    @Excel(name = "已交税费")
    private BigDecimal alreadyTaxMoney;

    /**
     * 应缴税费
     */
    @Excel(name = "应缴税费")
    private BigDecimal shouldTaxMoney;

    /**
     * 应退税费
     */
    @Excel(name = "应退税费")
    private BigDecimal recoverableTaxMoney;

    /**
     * 应补税费
     */
    @Excel(name = "应补税费")
    private BigDecimal supplementTaxMoney;

    /**
     *  0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税
     */
    @Excel(name = "税单状态" , replace = { "-_null","待确认_0","待退税_1","待补税_2","正常_3","已退税_4","已补税_5","待核对_6","待填报成本_7","待申报_8","已作废_9","待财务审核_10","审核不通过_11" })
    private Integer taxBillStatus;

    /**
     * 生成方式 1季度自动生成 2企业注销生成
     */
    @Excel(name = "税单类型" , replace = { "-_null","季度自动生成_1","企业注销生成_2" })
    private Integer generateType;
    /**
     * 个人所得税凭证上传状态 1-未上传 2-已上传 3-无需上传
     */
    @Excel(name = "个人所得税凭证上传状态" , replace = { "-_null","未上传_1","已上传_2","无需上传_3" })
    private String iitVouchersStatus;
    /**
     * 增值税凭证上传状态
     */
    @Excel(name = "增值税凭证上传状态" , replace =  { "-_null","未上传_1","已上传_2","无需上传_3" })
    private String vatVouchersStatus;
    /**
     * 确认时间
     */
    @Excel(name = "确认时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date affirmTime;
    /**
     * 完成时间
     */
    @Excel(name = "完成时间", replace = {
            "-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
    private Date completeTime;

    /**
     * 所属机构
     */
    @Excel(name = "所属机构")
    private String oemName;
    /**
     * 个人所得税凭证图片
     */
    private String iitVoucherPic;
    /**
     * 增值税凭证图片
     */
    private String vatVoucherPic;

    /**
     * 罚款凭证
     */
    private String ticketPic;

    /**
     * 是否有上传的完税凭证 0 - 没有 1-有
     */
    private Integer voucherFlag;

    /**
     * 园区税单id
     */
    private Long parkTaxBillId;

    /**
     * 所得税征收方式 1-查账征收 2-核定征收
     */
    private Integer incomeLevyType;

    /**
     * 应缴增值税
     */
    private BigDecimal vatShouldTaxMoney;

    /**
     * 应缴附加税
     */
    private BigDecimal additionalShouldTaxMoney;

    /**
     * 个税扣除金额
     * @param taxBillYear
     */
    private BigDecimal iitDeductionAmount;

    public void setTaxBillYear(Integer taxBillYear) {
        this.taxBillYear=taxBillYear;
        this.taxBillTime=taxBillYear+"年"+(this.taxBillTime==null?"":this.taxBillTime);
    }
    public void setTaxBillSeasonal(Integer taxBillSeasonal) {
        this.taxBillSeasonal=taxBillSeasonal;
        this.taxBillTime=(this.taxBillTime==null?"":this.taxBillTime)+taxBillSeasonal+"季度";
    }



}
