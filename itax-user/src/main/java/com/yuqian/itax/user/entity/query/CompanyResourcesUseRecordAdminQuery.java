package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResourcesUseRecordAdminQuery extends BaseQuery {
    private static final long serialVersionUID = -1L;

    /**
     * 会员账号
     */
    private String memberAccount;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
     */
    private Integer companyType;
    /**
     * 所属园区
     */
    private String parkName;
    /**
     * 所属园区id
     */
    private Long parkId;

    /**
     * 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照
     */
    private Integer resourcesType;
    /**
     * 审核状态 0-待审核 1-已审核 2-审核不通过 3-已撤销
     */
    private Integer auditStatus;


}
