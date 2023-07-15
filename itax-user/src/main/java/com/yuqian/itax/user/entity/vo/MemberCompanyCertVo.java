package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 我的企业证件管理
 * 
 * @Date: 2020年03月25日 09:42:12
 * @author yejian
 */
@Getter
@Setter
public class MemberCompanyCertVo implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String companyName;

	/**
	 * 证件列表
	 */
	@ApiModelProperty(value = "证件列表")
	List<MemberCompanyCertListVo> certList;


}
