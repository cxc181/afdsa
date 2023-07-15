package com.yuqian.itax.agent.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class OemSysConfigDetailVO implements Serializable {

    private static final long serialVersionUID = -1L;


    private  Long id;
    /**
     * 机构账号
     */
    private  String username;
    /**
     * 联系人电话
     */
    private  String phone;
    /**
     * 公司名称
     */
    private  String companyName;
    /**
     * 机构账号
     */
    private  String oemName;
    /**
     * 机构编号
     */
    private String oemCode;
    /**
     * 机构logo
     */
    private  String oemLogo;
    /**
     * 运营网址
     */
    private  String netAddress;
    /**
     * 添加时间
     */
    private Date addTime;
    /**
     * 机构状态 0-下架1-上架 2-暂停 3-待上架
     */
    private  Integer oemStatus;

    /**
     * 是否邀请人校验  0-不校验 1-校验
     */
    private Integer isInviterCheck;

    /**
     * 是否开通推广中心 0-否 1-是
     */
    private Integer isOpenPromotion;

    /**
     * 城市服务商员工上限
     */
    private Integer employeesLimit;
    /**
     * 城市服务商以下提现手续费率
     */
    private BigDecimal commissionServiceFeeRate;

    /**
     * 城市服务商提现手续费率
     */
    private BigDecimal diamondCommissionServiceFeeRate;

    /**
     *  是否接入国金助手 1-接入 0-不接入
     */
    private Integer isOpenChannel;

    /**
     *  国金助手地址
     */
    private String channelUrl;

    /**
     *  国金加密参数
     */
    private String secKey;

    /**
     * 工单审核方 1-平台客服  2-oem机构客服
     */
    private Integer workAuditWay;
}
