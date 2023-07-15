package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class CompanyResourcesUseRecordAdminVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;

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
     * 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照
     */
    private Integer resourcesType;

    /**
     * 用途描述
     */
    private String useDesc;

    /**
     * 申请时间
     */
    private Date addTime;

    /**
     * 审批时间
     */
    private Date updateTime;

    /**
     * 审核状态 0-待审核 1-已审核 2-审核不通过 3-已撤销
     */
    private Integer auditStatus;

    /**
     * 审核描述
     */
    private String auditDesc;
    /**
     * 申请人
     */
    private String addUser;
    /**
     * 所属OEM机构
     */
    private String oemName;
}
