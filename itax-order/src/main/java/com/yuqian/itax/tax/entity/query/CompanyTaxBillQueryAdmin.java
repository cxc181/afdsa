package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CompanyTaxBillQueryAdmin extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 税款所属期年
     */
    private Integer taxBillYear;

    /**
     * 税款所属期
     */
    private Integer taxBillSeasonal;
    /**
     * 园区id
     */
    private Long parkId;
    /**
     * 企业id
     */
    private Long companyId;
    /**
     * 园区税单id
     */
    private Long parkTaxBillId;

    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 经营者姓名
     */
    private String operatorName;
    /**
     * 税号
     */
    private String ein;
    /**
     * 注册账号
     */
    private String memberAccount;
    /**
     * 用户姓名
     */
    private String realName;
    /**
     *  税单状态 0-待确认 1-解析中 2-待上传 3-待确认 4-已确认
     */
    private Integer taxBillStatus;

    /**
     * 所属机构
     */
    private String oemCode;
    /**
     *个人所得税凭证状态  1-未上传 2-已上传 3-无需上传
     */
    private Integer iitVouchersStatus;
    /**
     *增值税凭证凭证状态  1-未上传 2-已上传 3-无需上传
     */
    private Integer vatVouchersStatus;

    /**
     * 本期是否开票 0-是 1-否
     */
    private Integer invoiceFlag;

    /**
     * 是否有罚款凭证 1有 2无
     */
    private Integer isTicketPic;

    /**
     * 生成方式 1季度自动生成 2企业注销生成
     */
    private Integer generateType;

}
