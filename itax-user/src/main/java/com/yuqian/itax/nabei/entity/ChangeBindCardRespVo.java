package com.yuqian.itax.nabei.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 修改签约账户响应参数实体
 */
@Getter
@Setter
public class ChangeBindCardRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 签名
	 */
	private String p3_hmac;

	/**
	 * 签约账户
	 */
	private String p4_accountNo;

	/**
	 * 银行名称
	 */
	private String p5_bankName;

	/**
	 * 银行简码
	 */
	private String p6_bankNameEn;

	/**
	 * 卡类型（0-银行卡，1-贷记卡，2-借记卡，3-准贷记卡，4-预付费卡，其他等）
	 */
	private String p7_cardType;
	
}
