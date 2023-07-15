package com.yuqian.itax.user.entity.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *  @Author: ni.jiang
 *  @Description: 用户实名认证接收参数DTO
 */
@Data
public class GJUserAuthDTO {

    /**
     * 国金机构编码
     */
    @NotBlank(message = "国金机构编码不能为空")
    private String channelOemCode;

    /**
     * 国金用户id
     */
    @NotNull(message = "国金用户id不能为空")
    private Long channelUserId;

    /**
     * 原手机号
     */
//    @NotBlank(message = "原手机号不能为空")
    private String oldMemberAccount;

    /**
     * 新手机号
     */
    private String memberAccount;

    /**
     *是否同步实名 0-否 1-是
     */
    @NotNull(message = "是否同步实名不能为空")
    private Integer isUpdateAutho;

    /**
     * 用户身份类型 0-未知 1-个人 2-企业
     */
    @NotNull(message = "用户身份不能为空")
    private Integer memberAuthType;

    /**
     *  用户姓名
     */
    private String memberName;

    /**
     *  身份证号码
     */
    private String idCardNo;

    /**
     *  身份证有效期
     */
    private String expireDate;

    /**
     *  身份证正面照
     */
    private String idCardFront;

    /**
     *  身份证反面照
     */
    private String idCardBack;

    /**
     *  身份证地址
     */
    private String idCardAddr;

    /**
     * 渠道服务商id
     */
    private Long channelServiceId;

    /**
     * 渠道员工id
     */
    private Long channelEmployeesId;

    /**
     * 渠道产品编码
     */
    private String channelProductCode;

}