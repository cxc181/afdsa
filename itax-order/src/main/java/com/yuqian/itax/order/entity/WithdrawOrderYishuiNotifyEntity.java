package com.yuqian.itax.order.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @Date: 2023/02/21
 * @author lmh
 */
@Getter
@Setter
@Table(name="t_e_withdraw_order_yishui_notify")
public class WithdrawOrderYishuiNotifyEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 商户号
	 */
	private String merchantCode;
	
	/**
	 * 易税回调原始请求参数
	 */
	private String paramTotal;
	
	/**
	 * 易税回调类型：create_batch_order：订单落地通知，charge_resout：充值入账结果通知 , payment_resout：支付结果通知
	 */
	private String paramType;

	/**
	 * 易税回调待解密请求参数
	 */
	private String paramResult;

	/**
	 * 易税回调明文请求参数
	 */
	private String paramResultPlaintext;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	
}
