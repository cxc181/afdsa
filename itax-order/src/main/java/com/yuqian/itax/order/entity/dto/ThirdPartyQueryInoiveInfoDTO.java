package com.yuqian.itax.order.entity.dto;

import com.yuqian.itax.park.entity.vo.TaxRulesVatRateVO;
import com.yuqian.itax.user.entity.vo.CompanyInvoiceCategoryJdVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ThirdPartyQueryInoiveInfoDTO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 机构编码
     */
    private String oemCode;
    /**
     * 接入方编码
     */
    private String accessPartyCode;
    /**
     * 企业id
     */
    private Long companyId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 发票类型 1-纸质发票 2-电子发票
     */
    private List<Integer> invoiceWayList;
    /**
     * 开票类型1、增值税普通发票；2、增值税专用发票
     */
    private List<Integer> invoiceTypeList;
    /**
     * 增值税率
     */
    private List<TaxRulesVatRateVO> rateList;
    /**
     * 剩余开票限额
     */
    private Long remainInvoiceAmount;
    /**
     * 开票类目
     */
    private List<CompanyInvoiceCategoryJdVO> categoryList;
    /**
     * 单笔最小开票金额
     */
    private Long rechargeAmountMinLimit;
    /**
     * 税号
     */
    private String ein;
    /**
     * 商品明细json
     */
    private String goodsDetails;
    /**
     * 税收分类编码集合
     */
    private List<String> taxCodes;
}
