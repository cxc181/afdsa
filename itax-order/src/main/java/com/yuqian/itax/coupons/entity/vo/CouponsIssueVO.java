package com.yuqian.itax.coupons.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 用户优惠券VO
 * @Author  liumenghao
 * @Date   2021/4/8
*/
@Getter
@Setter
public class CouponsIssueVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

    /**
     * 优惠券发放记录id
     */
    @ApiModelProperty(value = "优惠券发放记录id")
    private Long id;

    /**
     * 优惠券名称
     */
    @ApiModelProperty(value = "优惠券名称")
    private String couponsName;

    /**
     * 面额
     */
    @ApiModelProperty(value = "面额")
    private Long faceAmount;

    /**
     * 生效日期
     */
    @ApiModelProperty(value = "生效日期")
    @JSONField(format = "yyyy.MM.dd")
    private Date startDate;

    /**
     * 截止日期
     */
    @ApiModelProperty(value = "截止日期")
    @JSONField(format = "yyyy.MM.dd")
    private Date endDate;

    /**
     * 优惠券使用状态 0-未使用 1-已使用 2-已过期 3-已撤回
     */
    @ApiModelProperty(value = "优惠券使用状态")
    private Integer status;

    /**
     * 可用范围 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户
     */
    @ApiModelProperty(value = "可用范围")
    private String usableRange;

    /**
     * 是否可用 0-不可用 1-可用
     */
    @ApiModelProperty(value = "是否可用")
    private Integer usable;

    /**
     * 产品id
     */
    @ApiModelProperty(value = "产品id")
    private Long productId;

    /**
     * 企业类型
     */
    @ApiModelProperty(value = "企业类型")
    private Integer compType;
}
