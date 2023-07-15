package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CompanyCorporateAccountQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long Id;

    private String oemCode;

    /**
     * 会员账号
     */
    private String memberAccount;

    /**
     * 会员姓名
     */
    private String memberName;
    /**
     * 账户名
     */
    private String companyName;
    /**
     * 经营者姓名
     */
    private String operatorName;

    /**
     * 经营者手机号
     */
    private String operatorTel;
    /**
     * 账户状态
     */
    private Integer status;
    /**
     * 所属OEM机构
     */
    private String oemName;

    /**
     * 对公账户银行名称(开户行)
     */
    private String corporateAccountBankName;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 年费状态
     */
    private Integer overdueStatus;

    /**
     * 个体户状态
     */
    private Integer companyStatus;
}
