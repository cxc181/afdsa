package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 经营范围基础库列表VO
 * @Author  Kaven
 * @Date   2020/8/14 17:25
*/
@Getter
@Setter
public class BusinessScopeTaxCodeQuery  extends BaseQuery {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 经营范围
	 */
	@ApiModelProperty(value = "经营范围")
	private String businessScopName;

	/**
	 * 税收分类编码
	 */
	@ApiModelProperty(value = "税收分类编码")
	private String taxClassificationCode;

	/**
	 * 税收分类名称
	 */
	@ApiModelProperty(value = "税收分类名称")
	private String taxClassificationName;

	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

}