package com.yuqian.itax.system.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * oem机构园区行业黑名单
 * 
 * @Date: 2020年08月07日 10:38:39 
 * @author 蒋匿
 */
@Getter
@Setter
public class OemParkIndustryBlacklistRelaDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 机构编码
	 */
	@NotBlank(message="机构编码不能为空")
	private String oemCode;
	
	/**
	 * 园区id
	 */
	@NotNull(message="园区主键不能为空")
	private Long parkId;

	/**
	 * 行业id集合
	 */
	@NotNull(message="例外行业主键不能为空")
	private List<Long> ids;
}
