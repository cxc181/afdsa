package com.yuqian.itax.common.base.entity.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * 基础条件查询query
 *
 * @date: 2016年1月21日 上午12:37:53 
 * @author LiuXianTing
 */
@Getter
@Setter
public class  BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 默认显示数量为10条
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	/**
	 * 默认页数为第一页
	 */
	public static final int DEFAULT_PAGE_NUMBER = 1;
	
	/**
	 * 每页显示的数量
	 */
	@ApiModelProperty(value = "每页数量(默认10条)")
	private int pageSize = DEFAULT_PAGE_SIZE;
	
	/**
	 * 当前页
	 */
	@ApiModelProperty(value = "页码(默认第一页)")
	private int pageNumber = DEFAULT_PAGE_NUMBER;
	
	/**
	 * 排序字段
	 */
	@ApiModelProperty(value = "排序字段",hidden = true)
	private String orderBy;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人",hidden = true)
	private String createUser;

	/**
	 * 更新人
	 */
	@ApiModelProperty(value = "更新人",hidden = true)
	private String updateUser;

	/**
	 * 起始时间
	 */
	@ApiModelProperty(value = "起始时间",hidden = true)
	private String startDate;

	/**
	 * 结束时间
	 */
	@ApiModelProperty(value = "结束时间",hidden = true)
	private String endDate;

	/**
 	* 机构编码
 	*/
	@ApiModelProperty(value = "机构编码",hidden = true)
	private String oemCode;


}
