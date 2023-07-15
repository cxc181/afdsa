package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 行业
 * 
 * @Date: 2019年12月08日 20:37:33 
 * @author 蒋匿
 */
@Getter
@Setter
public class IndustryAdminVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 行业名称
	 */
	private String industryName;

	/**
	 * 园区ID
	 */
	private Long parkId;

	/**
	 * 是否选中
	 */
	private Integer isSelect;

}
