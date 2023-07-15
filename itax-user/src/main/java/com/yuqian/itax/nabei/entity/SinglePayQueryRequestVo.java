package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 单笔出款查询请求参数实体
 */
@Getter
@Setter
public class SinglePayQueryRequestVo implements Serializable {

	private static final long serialVersionUID = 7476995990063514365L;

	/**
	 * 出款服务商编号
	 */
	@SignInclude
	private String p1_taxNo;

	/**
	 * 出款订单号
	 */
	@SignInclude
	private String p2_orderNo;
	
	/**
	 * 签名
	 */
	private String p3_hmac;

}
