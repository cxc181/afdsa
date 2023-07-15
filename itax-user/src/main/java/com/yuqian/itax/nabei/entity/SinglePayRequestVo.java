package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *单笔出款申请请求参数实体
 */
@Getter
@Setter
public class SinglePayRequestVo implements Serializable {

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
	 * 签约姓名
	 */
	@SignInclude
	private String p3_accountName;
	
	/**
	 * 签约身份证号
	 */
	@SignInclude
	private String p4_idcardNo;
	
	/**
	 * 签约卡号
	 */
	@SignInclude
	private String p5_accountNo;

	/**
	 * 预留手机号
	 */
	@SignInclude
	private String p6_mobile;
	
	/**
	 * 出款金额
	 */
	@SignInclude
	private String p7_amount;
	
	/**
	 * 出款备注
	 */
	private String p8_remark;
	
	/**
	 * 通知地址
	 */
	private String p9_notifyUrl;

	/**
	 * 签名
	 */
	private String p10_hmac;

}
