package com.yuqian.itax.system.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @Description 园区经营范围查询query
 * @Author  Kaven
 * @Date   2020/8/14 17:25
*/
@Getter
@Setter
public class ParkBusinessScopeQuery extends BaseQuery {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "园区id")
	@NotNull(message = "园区id不能为空")
	private Long parkId;

	/**
	 * 经营范围名称
	 */
	private String businessScopeName;

	/**
	 * 经营范围列表
	 */
	private Set<String> businessScopes;
}