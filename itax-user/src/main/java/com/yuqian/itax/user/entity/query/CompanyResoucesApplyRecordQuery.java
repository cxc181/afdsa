package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResoucesApplyRecordQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;
    /**
     * 申请单编号
     */
    private String orderNo;
    /**
     * 申请类型  1-领用  2-归还
     */
    private String applyType;

    /**
     * 所属企业
     */
    private String CompanyName;
    /**
     * 申请状态  0-待发货  1-出库中 2-待签收 3-已签收 4-已取消
     */
    private Integer status;
    /**
     * 多个申请状态  0-待发货  1-出库中 2-待签收 3-已签收 4-已取消
     */
    private String statuss;

    /**
     * 证件名称   1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章 ，多个资源直接用 逗号分割
     */
    private String applyResouces;

    /**
     * 申请人账号
     */
    private String memberAccount;
    /**
     * 申请人姓名
     */
    private String memberName;

    /**
     * 所属OEM
     */
    private String oemName;

    /**
     * 园区ID
     */
    private Long parkId;
}
