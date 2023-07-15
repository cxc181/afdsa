package com.yuqian.itax.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 我的企业证件管理是否在园区返回vo
 * 
 * @Date: 2020年03月30日 14:42:12
 * @author yejian
 */
@Getter
@Setter
public class MemberCompanyCertInParkVo implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照 5-营业执照副本 6-发票章
	 */
	private Integer resourcesType;

	/**
	 * 资源类型名称
	 */
	@ApiModelProperty(value = "资源类型名称")
	private String resourcesTypeName;

}
