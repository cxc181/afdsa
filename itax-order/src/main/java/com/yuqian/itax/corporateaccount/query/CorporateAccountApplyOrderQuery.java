package com.yuqian.itax.corporateaccount.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CorporateAccountApplyOrderQuery extends BaseQuery implements Serializable {

    private static final long serialVersionUID = -1L;

    private String oemCode;

    private Long id;

    /**
     * 申请单编号
     */
    private String orderNo;
    /**
     * 申请会员账号
     */
    private String memberAccount;
    /**
     * 会员姓名
     */
    private String memberName;
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 申请单状态0-待付款,1-等待预约,2-已完成,3-已取消
     */
    private Integer orderStatus;
    /**
     * 所属OEM机构
     */
    private String oemName;
    /**
     * 付款完成时间(开始)
     */
    private String payTimeStartDate;
    /**
     * 付款完成时间（结束）
     */
    private String payTimeEndDate;
    /**
     * 所属园区id
     */
    private Long parkId;

    /**
     * 平台类型  1-平台 2-机构 3-园区 4-城市合伙人 5-城市合伙人
     */
    private Integer platformType;
}
