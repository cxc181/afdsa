package com.yuqian.itax.order.entity.vo;

import com.yuqian.itax.order.entity.CompanyCancelOrderEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业注销订单表
 * 
 * @Date: 2020年02月13日 15:33:29 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyCancelConformVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 企业名称
	 */
	private String companyName;
	
	/**
	 * 企业类型1-个体开户 2-个独开户 3-有限合伙 4-有限责任
	 */
	private Integer companyType;

	/**
	 * 经营者姓名
	 */
	private String operatorName;

	public CompanyCancelConformVO() {
	}

	public CompanyCancelConformVO(CompanyCancelOrderEntity companyCancelEntity, MemberCompanyEntity memberCompanyEntity) {
		this.orderNo = companyCancelEntity.getOrderNo();
		this.companyName = memberCompanyEntity.getCompanyName();
		this.companyType = memberCompanyEntity.getCompanyType();
		this.operatorName = memberCompanyEntity.getOperatorName();
	}
}
