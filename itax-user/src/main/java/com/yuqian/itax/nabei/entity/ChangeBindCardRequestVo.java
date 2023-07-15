package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 修改签约账户请求参数实体
 */
@Getter
@Setter
public class ChangeBindCardRequestVo implements Serializable {

	private static final long serialVersionUID = 7476995990063514365L;

	/**
	 * 签约服务商编号
	 */
	@SignInclude
	private String p1_taxNo;

	/**
	 * 签约姓名
	 */
	@SignInclude
	private String p2_accountName;

	/**
	 * 签约身份证号
	 */
	@SignInclude
	private String p3_idcardNo;
	
	/**
	 * 原签约卡号
	 */
	@SignInclude
	private String p4_orgAccountNo;
	
	/**
	 * 换绑签约卡号
	 */
	@SignInclude
	private String p5_accountNo;
	
	/**
	 * 换绑卡银行预留手机号
	 */
	@SignInclude
	private String p6_mobile;
	
	/**
	 * 签名
	 */
	private String p7_hmac;

}
