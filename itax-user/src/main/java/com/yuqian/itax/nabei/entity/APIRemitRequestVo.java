package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 对账文件下载请求参数实体
 */
@Getter
@Setter
public class APIRemitRequestVo implements Serializable {

	private static final long serialVersionUID = 7476995990063514365L;
	
	/**
	 * 服务商编号
	 */
	@SignInclude
	private String p1_taxNo;
	
	/**
	 * 对账日期yyyy-MM-dd
	 */
	@SignInclude
	private String p2_checkDate;
	
	/**
	 * 签名
	 */
	private String p3_hmac;

}
