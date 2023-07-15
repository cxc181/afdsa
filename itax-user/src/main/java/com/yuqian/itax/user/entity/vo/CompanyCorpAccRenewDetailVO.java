package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.io.Serializable;
import java.util.Date;

/**
 * 对公户年费续费详情VO
 * @Date: 2021/9/8
 * @author LiuMenghao
 */
@Getter
@Setter
public class CompanyCorpAccRenewDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 企业名称
	 */
	@ApiModelProperty(value = "企业名称 ")
	private String companyName;

	/**
	 * 开户行
	 */
	@ApiModelProperty(value = "开户行")
	private String corporateAccountBankName;

	/**
	 * 银行账户
	 */
	@ApiModelProperty(value = "银行账户")
	private String corporateAccount;

	/**
	 * 经营者姓名
	 */
	@ApiModelProperty(value = "经营者姓名 ")
	private String operatorName;

	/**
	 * 年费到期日
	 */
	@ApiModelProperty(value = "年费到期日 ")
	@JSONField(format = "yyyy-MM-dd")
	private Date expirationTime;

	/**
	 * 续费后到期日
	 */
	@ApiModelProperty(value = "续费后到期日 ")
	@JSONField(format = "yyyy-MM-dd")
	private Date endTime;

	/**
	 * 需支付年费
	 */
	@ApiModelProperty(value = "需支付年费")
	private Long prodAmount;
}