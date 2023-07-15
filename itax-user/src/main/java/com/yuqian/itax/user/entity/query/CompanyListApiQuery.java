package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 企业列表接收dto
 * @author：yejian
 * @Date：2020/07/15 15:12
 */
@Getter
@Setter
public class CompanyListApiQuery extends BaseQuery implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 会员账号
	 */
	@ApiModelProperty(value = "会员账号")
	private String regPhone;

	/**
	 * 企业名称，支持模糊查询
	 */
	@ApiModelProperty(value = "企业名称，支持模糊查询")
	private String companyName;

	/**
	 * 身份证号码
	 */
	@ApiModelProperty(value = "身份证号码")
	private String idCard;

	/**
	 * 状态：1->正常；2->禁用； 4->已注销 5->注销中
	 */
	@ApiModelProperty(value = "状态：1->正常；2->禁用 4->已注销 5->注销中")
	private Integer status;

}