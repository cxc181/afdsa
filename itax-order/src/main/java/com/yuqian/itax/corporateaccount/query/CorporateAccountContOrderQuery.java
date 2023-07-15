package com.yuqian.itax.corporateaccount.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorporateAccountContOrderQuery extends BaseQuery {

    private static final long serialVersionUID = -1L;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 注册账号
     */
    private String memberAccount;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 经营者姓名
     */
    private String operatorName;

    /**
     * oemCode
     */
    private String oemCode;

    /**
     * 园区id
     */
    private Long parkId;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 对公户开户行
     */
    private String corporateAccountBankName;

    /**
     * 创建时间开始时间
     */
    private String startAddTime;

    /**
     * 创建时间结束时间
     */
    private String endAddTime;

    /**
     * 完成时间开始时间
     */
    private String startCompelete;

    /**
     * 完成时间结束时间
     */
    private String endCompelete;


    /**
     * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    private Integer platformType;
}
