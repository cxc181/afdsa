package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/16 9:49
 *  @Description: 会员个人基本信息展示类
 */
@Getter
@Setter
public class MemberBaseInfoVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 会员账号
     */
    @ApiModelProperty(value = "会员账号")
    private String memberAccount;

    /**
     * 会员昵称
     */
    @ApiModelProperty(value = "会员昵称")
    private String memberName;
    /**
     * 会员头像
     */
    @ApiModelProperty(value = "会员头像")
    private String headImg;

    /**
     * 用户真实姓名
     */
    @ApiModelProperty(value = "用户真实姓名")
    private String realName;

    /**
     * 实名认证状态 0-未认证 1-认证成功 2-认证失败
     */
    private Integer authStatus;

    /**
     * 用户身份证号码
     */
    @ApiModelProperty(value = "用户身份证号码")
    private String idCardNo;

    /**
     * 用户身份证正面照地址
     */
    @ApiModelProperty(value = "用户身份证正面照地址")
    private String idCardFront;

    /**
     * 用户身份证反面照地址
     */
    @ApiModelProperty(value = "用户身份证反面照地址")
    private String idCardBack;

    /**
     * 身份证有效期
     */
    private String expireDate;

    /**
     * 身份证地址
     */
    private String idCardAddr;

    /**
     * 会员等级
     */
    @ApiModelProperty(value = "会员等级")
    private Integer levelNo;

    /**
     * 会员等级名称
     */
    @ApiModelProperty(value = "会员等级名称")
    private String levelName;

    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间")
    private Date addTime;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称")
    private String oemName;

    /**
     * 机构客服电话
     */
    @ApiModelProperty(value = "机构客服电话")
    private String oemMobile;

    /**
     * 开票类目id
     */
    @ApiModelProperty(value = "开票类目id")
    private String categoryBaseId;

    /**
     * 开票类目名称
     */
    @ApiModelProperty(value = "开票类目名称")
    private String categoryName;

    /**
     * 账户状态  1-正常 0-禁用 2-注销
     */
    @ApiModelProperty(value = "账户状态 1-正常 0-禁用 2-注销")
    private Integer status;

    /**
     * 邀请人id
     */
    @ApiModelProperty(value = "邀请人id")
    private Long parentMemberId;

    /**
     * 邀请人账号
     */
    @ApiModelProperty(value = "邀请人账号")
    private String parentMemberAccount;

    /**
     * 所在省编码
     */
    @ApiModelProperty(value = "所在省编码")
    private String provinceCode;

    /**
     * 所在市编码
     */
    @ApiModelProperty(value = "所在市编码")
    private String cityCode;

    /**
     * 所在省名称
     */
    @ApiModelProperty(value = "所在省名称")
    private String provinceName;

    /**
     * 所在市名称
     */
    @ApiModelProperty(value = "所在市名称")
    private String cityName;

    /**
     * 是否有已完成的开户企业 0-无 1-有
     */
    @ApiModelProperty(value = "是否有已完成的开户企业")
    private Integer hasFinishedCom;

    /**
     * 推广类型 1-散客 2-直客 3-顶级直客
     */
    private Integer extendType;

    private Integer hasIndividual;// 有无个体户标识 0-无 1-有
    /**
     * 是否手动降级 0-未降级 1-已手动降级
     */
    private Integer isDemotion;

    /**
     * 服务商id  优先显示渠道员工id，如渠道员工id为空则显示渠道服务商id
     */
    private Long serviceId;

    /**
     * 所属服务商名称
     */
    @ApiModelProperty(value = "所属服务商名称")
    private String channelServiceName;

    /**
     * 所属服务商手机号
     */
    @ApiModelProperty(value = "所属服务商手机号")
    private String channelServiceAccount;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 渠道产品编码
     */
    private String channelProductCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道logo
     */
    private String channelLogo;

    /**
     * 会员身份类型 0-未知 1-个人 2-企业
     */
    private Integer memberAuthType;

    /**
     * appid
     */
    private String appId;

    /**
     * 是否露出对公户
     */
    private Integer isShowCorporate;

    /**
     * 身份认证是否已过期 0-否 1-是
     */
    private int isExpiredDocuments;
}
