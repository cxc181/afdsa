package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 园区税单文件上传字段实体
 * @Date: 2020年12月03日 10:36:14
 * @author 蒋匿
 */
@Getter
@Setter
public class ParkTaxBillUploadVO implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * 园区税单id
     */
    private Long parkTaxBill;

    /**
     * 企业id
     */
    private Long companyId;
    /**
     * 企业名称
     */
    @Excel(name = "企业名称")
    private String companyName;
    /**
     *批次号
     */
    private String batchNumber;
    /**
     * 税号
     */
    @Excel(name = "税号")
    private String ein;
    /**
     * 季度开票金额
     */
    @Excel(name = "本季开票金额")
    private BigDecimal invoiceAmount;

    /**
     * 作废/红冲金额
     */
    /*@Excel(name = "作废红冲金额")*/
    private BigDecimal cancellationAmount;
    /**
     * 总税费
     */
    @Excel(name = "总税费")
    private BigDecimal shouldTaxMoney;
    /**
     * 增值税
     */
    @Excel(name = "增值税")
    private BigDecimal vatShouldTaxMoney;
    /**
     * 附加税
     */
    @Excel(name = "附加税")
    private BigDecimal additionalShouldTaxMoney;
    /**
     * 所得税
     */
    @Excel(name = "所得税")
    private BigDecimal incomeShouldTaxMoney;
    /**
     * 备注
     */
    @Excel(name = "备注")
    private String remark;

    @Excel(name = "失败原因")
    private String failed;

    private String fileUrl;
    /**
     *解析状态 0-解析成功 1-解析失败
     */
    private Integer status;

}
