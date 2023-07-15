package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 我的企业证件管理列表返回vo
 * 
 * @Date: 2020年03月30日 14:42:12
 * @author yejian
 */
@Getter
@Setter
public class MemberCompanyCertListVo implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照 5-营业执照副本 6-发票章
	 */
	private Integer resourcesType;

	/**
	 * 资源类型名称
	 */
	@ApiModelProperty(value = "资源类型名称")
	private String resourcesTypeName;

	/**
	 * 所在地
	 */
	@ApiModelProperty(value = "所在地")
	private String address;

	/**
	 * 是否在园区 0-不在园区 1-在园区
	 */
	@ApiModelProperty(value = "是否在园区 0-不在园区 1-在园区")
	private Integer isInPark;

	/**
	 * 是否在申请 0-没有申请 1-已申请
	 */
	@ApiModelProperty(value = "是否在申请 0-没有申请 1-已申请")
	private Integer isInApply;


}
