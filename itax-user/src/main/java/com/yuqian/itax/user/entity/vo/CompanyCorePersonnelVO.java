package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Setter
@Getter
public class CompanyCorePersonnelVO implements Serializable {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 企业id
     */
    private Long companyId;

    /**
     * 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     */
    private Integer companyType;

    /**
     * 成员类型  1-经理 2- 监事 3-执行董事 4-财务 5-无职务,多个类型之间用逗号分割
     */
    private String personnelType;

    /**
     * 合伙人类型 1-普通合伙 2-有限合伙
     */
    private Integer partnerType;

    /**
     * 是否法人 0-否 1-是
     */
    private Integer isLegalPerson;

    /**
     * 是否执行事务合伙人 0-否 1-是
     */
    private Integer isExecutivePartner;

    /**
     * 是否股东 0-否 1-是
     */
    private Integer isShareholder;

    /**
     * 身份类型 1-自然人 2-企业
     */
    private Integer identityType;

    /**
     * 姓名
     */
    private String personnelName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 证件号
     */
    private String certificateNo;

    /**
     * 证件地址
     */
    private String certificateAddr;

    /**
     * 证件有效期
     */
    private String expireDate;

    /**
     * 身份证正面
     */
    private String idCardFront;

    /**
     * 身份证正面地址
     */
    private String idCardFrontUrl;

    /**
     * 身份证反面
     */
    private String idCardReverse;

    /**
     * 身份证反面地址
     */
    private String idCardReverseUrl;

    /**
     * 企业营业执照
     */
    private String businessLicense;

    /**
     * 企业营业执照地址
     */
    private String businessLicenseUrl;

    /**
     * 投资金额(万元)
     */
    private BigDecimal investmentAmount;

    /**
     * 占股比例(小数)
     */
    private BigDecimal shareProportion;

    /**
     * 授权后图片数组
     */
    private List<String> imgUrl = new ArrayList<>();

    /**
     * 成员标签数组  执行事务合伙人,普通合伙人,有限合伙人,股东,经营者,法人，经理，监事，执行董事，财务
     */
    private List<String> labelList = new ArrayList<>();
    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 添加人
     */
    private String addUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 备注
     */
    private String remark;

    /**
     * 委派方id
     */
    private Long appointPartyId;

    /**
     * 委派方名称
     */
    private String appointPartyName;
}
