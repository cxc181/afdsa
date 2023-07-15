package com.yuqian.itax.user.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 *  @Author: lmh
 *  @Date: 2022/6/29
 *  @Description: 企业核心成员接收参数DTO
 */
@Getter
@Setter
public class CompanyCorePersonnelDTO {

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 工单id
     */
    private Long workOrderId;

    /**
     * 操作类型 0-操作合伙人/股东 1-操作成员(有职务)
     */
    private int controlType;

    /**
     * 操作用户
     */
    private String addUser;

    /**
     * 成员id（成员id不为空时，视为编辑成员）
     */
    private Long id;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 成员类型 1-经理 2- 监事 3-执行董事 4-财务 5-无职务 多个类型之间用逗号分割
     */
    @NotNull(message = "成员类型不能为空")
    private Integer personnelType;

    /**
     * 身份类型 1-自然人 2-企业
     */
    @NotNull(message = "身份类型不能为空")
    private Integer identityType;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String personnelName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 证件号
     */
    @NotBlank(message = "证件号不能为空")
    private String certificateNo;

    /**
     * 证件地址
     */
    @NotBlank(message = "证件地址不能为空")
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
     * 身份证反面
     */
    private String idCardReverse;

    /**
     * 企业营业执照
     */
    private String businessLicense;

    /**
     * 是否股东/合伙人 0-否 1-是
     */
    private Integer isShareholder;

    /**
     * 投资金额(万元)
     */
    private BigDecimal investmentAmount;

    /**
     * 占股比例(小数)
     */
    private BigDecimal shareProportion;

    /**
     * 企业类型 1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
     */
    @NotNull(message = "企业类型不能为空")
    private Integer companyType;

    /**
     * 合伙人类型 1-普通 2-有限合伙
     */
    private Integer partnerType;

    /**
     * 是否执行事务合伙人 0-否 1-是
     */
    private Integer isExecutivePartner;

    /**
     * 是否法人 0-否 1-是
     */
    private int isLegalPerson;

    /**
     * 委派方id
     */
    private Long appointPartyId;

    /**
     * 映射股东/合伙人id
     */
    private Long mappingShareholdersId;
}