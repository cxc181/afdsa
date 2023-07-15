package com.yuqian.itax.system.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 税务登记号查询实体
 * 
 * @Date: 2020年02月21日 9:39:30
 * @author yejian
 */
@Getter
@Setter
public class CreditCodeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 企业名称
	 */
	@ApiModelProperty(value = "企业名称")
	private String ename;

	/**
	 * 社会统一信用代码（纳税人识别号）
	 */
	@ApiModelProperty(value = "社会统一信用代码（纳税人识别号）")
	private String creditCode;

	/**
	 * 地址
	 */
	@ApiModelProperty(value = "地址")
	private String address;

	/**
	 * 企业类型
	 */
	@ApiModelProperty(value = "企业类型")
	private String econKind;

	/**
	 * 联系电话
	 */
	@ApiModelProperty(value = "联系电话")
	private String tel;

	/**
	 * 企业状态
	 */
	@ApiModelProperty(value = "企业状态")
	private String status;

	/**
	 * 开户行
	 */
	@ApiModelProperty(value = "开户行")
	private String bank;

	/**
	 * 开户行账号
	 */
	@ApiModelProperty(value = "开户行账号")
	private String bankAccount;

}
