package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: lmh
 *  @Date: 2021/8/12
 *  @Description: 企业注册订单返回-接入方使用
 */
@Getter
@Setter
public class RegisterOrderOfAccessPartyVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业名称
	 */
	private String registerName;

	/**
	 * 经营者姓名
	 */
	private String operatorName;

    /**
     * 订单状态 工商注册(0-待电子签字,1-待视 频认证,2-审核中,3-待付款,4-待领证,5-已完成,6-已取消,7-审核失败,8-核名驳回,9-待身份验证，10-待设立登记、11-待提交签名、12-签名待确认)
     */
    private Integer orderStatus;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 园区
	 */
	private String parkName;

	/**
	 * 支付金额
	 */
	private Long payAmount;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 手机号
	 */
	private String memberPhone;

	/**
	 * 费用承担方
	 */
	private String payerName;

	/**
	 * 是否自费 1-自费 2-承担方
	 */
	private Integer isSelfPaying;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 支付凭证
	 */
	private String paymentVoucher;

	/**
	 * 税号
	 */
	private String ein;
}
