package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * h5签约申请请求参数实体
 */
@Getter
@Setter
public class H5SignRequestVo implements Serializable {

	private static final long serialVersionUID = 7476995990063514365L;

	/**
	 * 出款服务商编号
	 */
	@SignInclude
	private String p1_taxNo;

	/**
	 * 签约订单号
	 */
	@SignInclude
	private String p2_orderNo;
	
	/**
	 * 签约姓名
	 */

	private String p3_accountName;
	
	/**
	 * 签约身份证号
	 */
	private String p4_idcardNo;

	/**
	 * 签约账户类型 1-银行卡 2-支付宝 3-微信
	 */
	@SignInclude
	private String p5_accountType;
	
	/**
	 * 签约卡号
	 */
	private String p6_accountNo;

	/**
	 * 预留手机号
	 */
	private String p7_mobile;
	
	/**
	 * 签约通知地址
	 */
	private String p8_notifyUrl;

	/**
	 * 接入端类型
	 * app：安卓/iOS APP
	 * wxH5：微信公众号
	 * wxApp：微信小程序
	 * zfbH5：支付宝生活号
	 * zfbApp：支付宝小程序
	 */
	@SignInclude
	private String p9_platType;

	/**
	 * 签名
	 */
	private String p_hmac;

}
