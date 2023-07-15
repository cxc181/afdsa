package com.yuqian.itax.agent.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * oem机构园区行业黑名单
 * 
 * @Date: 2020年08月07日 10:38:39 
 * @author 蒋匿
 */
@Getter
@Setter
public class OemParkIndustryAdminDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 园区id
	 */
	@NotNull(message = "请选择新增园区")
	private Long parkId;

	/**
	 * 协议id
	 */
	private Long agreementTemplateId;

	/**
	 * 行业黑名单id集合
	 */
	private List<Long> industryIds;

}
