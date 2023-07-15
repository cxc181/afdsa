package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 签约注册查询请求参数实体
 */
@Getter
@Setter
public class APISignQueryRequest implements Serializable {

	private static final long serialVersionUID = 7476995990063514365L;

	/**
	 * 签约服务商编号
	 */
	@SignInclude
	private String p1_taxNo;

	/**
	 * 用户身份证号码
	 */
	@SignInclude
	private String p2_idcardNo;

	/**
	 * 银行卡号
	 */
	@SignInclude
	private String  extAccountNo;
	
	/**
	 * 签名
	 */
	private String p3_hmac;

}
