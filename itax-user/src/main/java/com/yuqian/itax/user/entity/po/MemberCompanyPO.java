package com.yuqian.itax.user.entity.po;

import com.yuqian.itax.system.entity.vo.InvoiceCategoryBaseStringVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class MemberCompanyPO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 经营者联系方式
     */
    private String operatorTel;
    /**
     * 经营者邮箱
     */
    private String operatorEmail;
    /**
     * 税号
     */
    private String ein;
    /**
     * 开票类目
     */
    private List<InvoiceCategoryBaseStringVO> categories;
    /**
     * 经营范围
     */
    private String businessScope;
    /**
     * 经营地址
     */
    private String businessAddress;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 营业执照(副本)
     */
    private String businessLicenseCopy;

    /**
     * 税务登记日期
     */
    private String taxRegDate;

    /**
     * 核定经营额(分）
     */
    private Long approvedTurnover;

    /**
     * 委托注册协议图片,多个文件用逗号分隔 ,
     */
    private String userAgreementImgs;

}
