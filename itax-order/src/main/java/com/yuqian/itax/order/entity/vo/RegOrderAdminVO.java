package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.order.entity.RegisterOrderEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 工商注册订单
 * 
 * @Date: 2019年12月07日 20:06:37 
 * @author 蒋匿
 */
@Getter
@Setter
public class RegOrderAdminVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 经营者姓名
	 */
	@ApiModelProperty(value = "经营者姓名")
	private String operatorName;

	/**
	 * 注册名称
	 */
	@ApiModelProperty(value = "企业名称")
	private String registeredName;

	/**
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任',
	 */
	@ApiModelProperty(value = "企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任")
	private Integer companyType;

	public RegOrderAdminVO(RegisterOrderEntity entity) {
		this.operatorName = entity.getOperatorName();
		this.registeredName = entity.getRegisteredName();
		this.companyType = entity.getCompanyType();
	}
}
