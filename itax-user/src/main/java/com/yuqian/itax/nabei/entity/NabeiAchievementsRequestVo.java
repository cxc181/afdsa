package com.yuqian.itax.nabei.entity;

import com.yuqian.itax.util.util.nabei.SignInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 纳呗提交成果请求参数
 */
@Getter
@Setter
public class NabeiAchievementsRequestVo implements Serializable {

	private static final long serialVersionUID = 7476995990063514365L;

	/**
	 * 出款服务商编号
	 */
	@SignInclude
	private String p1_taxNo;

	/**
	 * 姓名
	 */
	@SignInclude
	private String p2_name;
	
	/**
	 * 签约身份证号
	 */
	@SignInclude
	private String p3_idcardNo;

	/**
	 * 支付订单号
	 */
	@SignInclude
	private String p4_payOrderNo;

	/**
	 * 任务编号
	 */
	@SignInclude
	private String p5_taskNo;
	
	/**
	 * 成果数量
	 */
	@SignInclude
	private String p6_submitCount;

	/**
	 * 成果金额
	 */
	@SignInclude
	private String p7_submitAmount;
	
	/**
	 * 成 果 提 交 时 间
	 */
	@SignInclude
	private String p8_submitDate;

	/**
	 * 简要描述
	 */
	@SignInclude
	private String p9_remark;

	/**
	 * 签名
	 */
	private String p_hmac;

}
