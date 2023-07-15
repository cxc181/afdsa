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
public class CompanyRegisterOrderVO implements Serializable {

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
     * 订单状态 0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，
     *          10-待设立登记、11-待提交签名 12-签名待确认 13-待创建
     */
    private Integer orderStatus;

    /**
     * 注册资本
     */
    private BigDecimal registeredCapital;
}
