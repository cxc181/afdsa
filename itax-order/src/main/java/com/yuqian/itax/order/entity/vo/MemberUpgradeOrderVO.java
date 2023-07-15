package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/25 16:55
 *  @Description: 会员升级订单返回bean-拓展宝
 */
@Getter
@Setter
public class MemberUpgradeOrderVO implements Serializable {
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
     * 购买会员等级 1-税务顾问 2-城市服务商
     */
    private Integer buyLevelNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户邀请码
     */
    private String inviteCode;

    /**
     * 会费
     */
    private Long payAmount;
}
