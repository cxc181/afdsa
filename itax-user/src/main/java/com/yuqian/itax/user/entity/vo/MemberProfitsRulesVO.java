package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class MemberProfitsRulesVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 机构编码
     */
    private String oemCode;

    /**
     * 会员等级  0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
     */
    private Integer userLevel;

    /**
     * 会费分润
     */
    private BigDecimal membershipFee;
    /**
     * 托管费分润率
     */
    private BigDecimal profitsEntrustFeeRate;

    /**
     * 下级是平级时托管费分润率
     */
    private BigDecimal profitsPeersTwoEntrustFeeRate;

    /**
     * 下级是平级时会费分润率
     */
    private BigDecimal profitsPeersTwoMembershipFee;

    /**
     * 服务费分润率
     */
    private BigDecimal serviceFeeRate;

    /**
     * 下级是平级时服务费分润率
     */
    private BigDecimal profitsPeersTwoServiceFeeRate;


    /**
     * 消费折扣
     */
    private BigDecimal consumptionDiscount;

    /**
     * 状态 0-不可用 1-可用
     */
    private Integer status;

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
     * 机构名称
     */
    private String oemName;



}
