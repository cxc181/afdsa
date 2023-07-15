package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 16:30
 *  @Description: 企业注销订单返回bean-拓展宝
 */
@Getter
@Setter
public class ComCancelOrderVO implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 注册手机号
     */
    private String regPhone;

    /**
     * 订单状态 0-待创建 1-待付款 2-待审核 3-出票中 4-待发货 5-出库中 6-待收货 7-已签收  8-已取消
     */
    private Integer orderStatus;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 会员等级
     */
    private Integer levelNo;

    /**
     * 注销企业名称
     */
    private String companyName;

    /**
     * 注销企业类型
     */
    private Integer companyType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户邀请码
     */
    private String inviteCode;

    /**
     * 园区
     */
    private String parkName;

    /**
     * 支付金额
     */
    private Long payAmount;
}
