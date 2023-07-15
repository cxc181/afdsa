package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 常用成本项VO
 * 
 * @Date: 2022/3/10
 * @author lmh
 */
@Getter
@Setter
public class CommonCostItemVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * 成本项名称
	 */
	private String costItemName;

	/**
	 * 成本项类型 1-用户自定义成本项 2-系统固定成本项
	 */
	private int type;
}
