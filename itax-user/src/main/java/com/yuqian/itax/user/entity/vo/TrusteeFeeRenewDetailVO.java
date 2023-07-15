package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 托管费续费详情VO
 * @Date: 2021/2/4 16：23
 * @author LiuMenghao
 */
@Getter
@Setter
public class TrusteeFeeRenewDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 企业名称
	 */
	@ApiModelProperty(value = "企业名称 ")
	private String companyName;

	/**
	 * 企业类型
	 * 1-个体户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	@ApiModelProperty(value = "企业类型 ")
	private Integer companyType;

	/**
	 * 经营者姓名
	 */
	@ApiModelProperty(value = "经营者姓名 ")
	private String operatorName;

	/**
	 * 手机号码
	 */
	@ApiModelProperty(value = "手机号码 ")
	private String operatorTel;

	/**
	 * 所属园区
	 */
	@ApiModelProperty(value = "所属园区 ")
	private String parkName;

	/**
	 * 托管费到期日
	 */
	@ApiModelProperty(value = "托管费到期日 ")
	@JSONField(format = "yyyy-MM-dd")
	private Date endTime;

	/**
	 * 需支付托管费
	 */
	@ApiModelProperty(value = "需支付托管费")
	private Long trusteeFee;

	/**
	 * 会员折扣
	 */
	@ApiModelProperty(value = "会员折扣")
	private Long memberDiscount;

	/**
	 * 特价活动产品id
	 */
	@ApiModelProperty(value = "特价活动产品id")
	private Long discountActivityId;
}