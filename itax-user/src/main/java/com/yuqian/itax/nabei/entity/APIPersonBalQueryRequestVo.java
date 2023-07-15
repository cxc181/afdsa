package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户额度查询请求参数实体
 */
@Getter
@Setter
public class APIPersonBalQueryRequestVo implements Serializable {

	private static final long serialVersionUID = 7476995990063514365L;

	/**
	 * 签约服务商编号
	 */
	@SignInclude
	private String p1_taxNo;

	/**
	 * 用户身份证号
	 */
	@SignInclude
	private String p2_idcardNo;
	
	/**
	 * 签名
	 */
	private String p3_hmac;

}
