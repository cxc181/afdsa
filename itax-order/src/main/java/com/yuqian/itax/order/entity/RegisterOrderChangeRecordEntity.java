package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * 工商注册订单变更记录
 * 
 * @Date: 2019年12月07日 19:54:45 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_register_order_change_record")
public class RegisterOrderChangeRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 订单状态 0-待提交  1-审核中  2-待付款 3-待名称核准 4-待设立登记 5-待用户签名 6-待经办人签名 7-待领证 8-已完成
	 */
	private Integer orderStatus;
	
	/**
	 * 经营者姓名
	 */
	private String operatorName;
	
	/**
	 * 字号
	 */
	private String shopName;
	
	/**
	 * 组织形式  -- 暂时预留
	 */
	private Integer organizationForm;
	
	/**
	 * 行业类型id
	 */
	private Long industryId;
	
	/**
	 * 经营地址 网址
	 */
	private String businessAddress;
	
	/**
	 * 联系电话
	 */
	private String contactPhone;
	
	/**
	 * 电子邮箱
	 */
	private String email;
	
	/**
	 * 身份证正面
	 */
	private String idCardFront;
	
	/**
	 * 身份证反面
	 */
	private String idCardReverse;
	
	/**
	 * 身份证号码
	 */
	private String idCardNumber;

	/**
	 * 身份证有效期
	 */
	private String expireDate;
	
	/**
	 * 核定税种
	 */
	private String ratifyTax;
	
	/**
	 * 经营范围
	 */
	private String businessScope;
	
	/**
	 * 注册名称
	 */
	private String registeredName;
	
	/**
	 * 支付订单编号
	 */
	private String payOrderNo;
	
	/**
	 * 签名单
	 */
	private String signImg;
	
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
	 * 登记文件
	 */
	private String registFile;
	
	/**
	 * 通知次数
	 */
	private Integer alertNumber;
	
	/**
	 * 经办人账号
	 */
	private String agentAccount;
	
	/**
	 * 添加时间
	 */
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
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
	 */
	private Integer companyType;

	/**
	 * 短视频地址
	 */
	private String videoAddr;

	/**
	 * 专属客服电话
	 */
	private String customerServicePhone;

	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	private Integer isOther;

	/**
	 * 优惠卷发放记录id
	 */
	private Long couponsIssueId;

	/**
	 * 驳回项，多个项之间用逗号分割 1-字号 2-身份证 3-视频
	 */
	private String rejectedItem;

	/**
	 * 支付凭证
	 */
	private String paymentVoucher;

	/**
	 * 身份证地址
	 */
	private String idCardAddr;

	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 纳税人类型 1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 是否自动生成（企业信息） 0-否 1-是
	 */
	private Integer isAutoCreate;
}
