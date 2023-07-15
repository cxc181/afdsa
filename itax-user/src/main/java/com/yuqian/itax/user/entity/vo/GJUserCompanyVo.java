package com.yuqian.itax.user.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 国金企业信息
 * @Date: 2020年07月15日 15:42:12
 * @author yejian
 */
@Getter
@Setter
public class GJUserCompanyVo implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 云财oemcode
	 */
	private String oemCode;

	/**
	 * 是否注销 1-未注销 2-已注销
	 */
	private Integer isCancel;

	/**
	 * 月开票金额（已完成）
	 */
	private Long monthInvoiceAmount;

	/**
	 * 年开票金额（已完成）
	 */
	private Long yearInvoiceAmount;

	/**
	 * 累计开票金额（已完成）
	 */
	private Long totalInvoiceAmount;
}
