package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 企业列表查询条件
 * @author：yejian
 * @Date：2020/03/11 15:12
 */
@Getter
@Setter
public class CompanyListQuery extends BaseQuery implements Serializable {

	private static final long serialVersionUID = -1L;
	/**
	 * 查询类型：1->开票企业列表 ；2->我的企业列表 ; 3->选择开票主体
	 */
	@NotNull(message="查询类型不能为空")
	@ApiModelProperty(value = "查询类型：1->开票企业列表 ；2->我的企业列表 ; 3->选择开票主体",required = true)
	private Long type;

	private String categoryBaseId;//开票类目Id

	private List<String> categoryBaseIds;//开票类目id集合
}