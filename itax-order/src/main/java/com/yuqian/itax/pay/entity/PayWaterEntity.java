package com.yuqian.itax.pay.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 支付流水
 * 
 * @Date: 2019年12月07日 20:13:40 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_pay_water")
public class PayWaterEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 支付流水号
	 */
	private String payNo;
	
	/**
	 * 订单号
	 */
	@Excel(name = "订单编号")
	private String orderNo;
	
	/**
	 * 外部流水号
	 */
	private String externalOrderNo;
	
	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 用户类型 1-会员 2-城市合伙人 3-合伙人 4-平台 5-管理员 6-其他
	 */
	private Integer userType;
	
	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 机构名称
	 */
	@Excel(name = "机构名称")
	private String oemName;

	/**
	 * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商开户 6-开票 7-用户升级 8-工商注销
	 */

	@Excel(name = "订单类型", replace = { "充值_1","代理充值_2","提现_3","代理提现_4","工商开户_5","开票_6","用户升级_7","工商注销_8"," 企业资源申请_9"}, height = 10, width = 22)
	private Integer orderType;

	/**
	 * 订单金额
	 */
	private Long orderAmount;
	
	/**
	 * 优惠金额
	 */
	private Long discountAmount;
	
	/**
	 * 支付金额
	 */
	private Long payAmount;

	/**
	 * 流水类型 1-充值，2-提现 ，3-余额支付，4-第三方支付，5-退款，6-对公户提现，7-企业退税
	 */
	private Integer payWaterType;

	/**
	 * 支付方式  1-微信 2-余额 3-支付宝 4-快捷支付 5-字节跳动
	 */
	@Excel(name = "订单类型", replace = { "微信_1","余额_2","支付宝_3","快捷支付_4","抖音_5"}, height = 10, width = 22)
	private Integer payWay;

	/**
	 * 支付时间
	 */
	private Date payTime;
	
	/**
	 * 支付状态 0-待支付 1-支付中 2 -支付成功 3-支付失败
	 */
	private Integer payStatus;
	
	/**
	 * 支付账户
	 */
	@Excel(name = "支付账号")
	private String payAccount;
	
	/**
	 * 支付银行
	 */
	@Excel(name = "银行名称")
	private String payBank;
	
	/**
	 * 添加时间
	 */
	@Excel(name = "创建时间", replace = {"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
	private Date addTime;

	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 上游状态码
	 */
	private String upStatusCode;

	/**
	 * 上游返回信息
	 */
	private String upResultMsg;

	/**
	 * 支付通道 1-微信 2-余额 3-支付宝 4-易宝支付 5-建行 6-北京代付 7-纳呗
	 */
	private Integer payChannels;

	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
	 */
	private Integer walletType;

	/**
	 * 手续费
	 */
	@ApiModelProperty(value = "手续费")
	private Long serviceFee;

	/**
	 * 手续费率
	 */
	@ApiModelProperty(value = "手续费率")
	private BigDecimal serviceFeeRate;

	/**
	 * 打款凭证
	 */
	@ApiModelProperty(value = "打款凭证")
	private String payPic;

	/**
	 * 退款状态 1-退款中 2-退款成功 3-退款失败
	 */
	@ApiModelProperty(value = "退款状态")
	private Integer refundStatus;

	/**
	 * 收单机构oemcode
	 */
	private String otherPayOemcode;
}
