package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
public class TaxBillInfoToMemberVO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 园区名称
     */
    private String parkName;
    /**
     * 年
     */
    private Integer taxBillYear;
    /**
     * 季度
     */
    private Integer taxBillSeasonal;
    /**
     * 用户电话
     */
    private String memberPhone;
    /**
     * 退费企业数
     */
    private Integer recoverable;
    /**
     * 补税企业数
     */
    private Integer supplement;
    /**
     * 会员id
     */
    private Long memberId;

    private String oemCode;

    private Integer years;

    private Integer months;

    private Integer days;

    /**
     * 本期开票金额
     */
    private Long invoiceMoney;
}
