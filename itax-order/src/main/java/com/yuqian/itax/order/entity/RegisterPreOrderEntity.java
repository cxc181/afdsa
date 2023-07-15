package com.yuqian.itax.order.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 工商注册预订单
 * 
 * @Date: 2022年06月27日 17:54:10 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_register_pre_order")
public class RegisterPreOrderEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 法人姓名
	 */
	private String operatorName;
	
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
	 * 身份证地址
	 */
	private String idCardAddr;
	
	/**
	 * 身份证有效期，格式如2010/01/01-2020/01/01
	 */
	private String expireDate;
	
	/**
	 * 字号
	 */
	private String shopName;
	
	/**
	 * 备选字号1
	 */
	private String shopNameOne;
	
	/**
	 * 备选字号2
	 */
	private String shopNameTwo;
	
	/**
	 * 组织形式  -- 暂时预留
	 */
	private Integer organizationForm;
	
	/**
	 * 行业类型id
	 */
	private Long industryId;
	
	/**
	 * 经营地址
	 */
	private String businessAddress;
	
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
	 * 通知次数
	 */
	private Integer alertNumber;
	
	/**
	 * 登记文件
	 */
	private String registFile;
	
	/**
	 * 经办人账号
	 */
	private String agentAccount;
	
	/**
	 * 专属客服电话
	 */
	private String customerServicePhone;
	
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
	 * 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
	
	/**
	 * 短视频地址
	 */
	private String videoAddr;
	
	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	private Integer isOther;
	
	/**
	 * 是否微信授权 0-未授权 1-已授权
	 */
	private Integer isWechatAuthorization;
	
	/**
	 * 微信消息通知结果 0-失败 1-成功
	 */
	private Integer wechatMessageNoticeResult;
	
	/**
	 * 微信消息通知失败原因
	 */
	private String wechatMessageErrorCause;
	
	/**
	 * 微信消息通知时间
	 */
	private Date wechatMessageNoticeTime;
	
	/**
	 * 示例名称
	 */
	private String exampleName;
	
	/**
	 * 微信消息模板id
	 */
	private Long wechatMessageTemplateId;
	
	/**
	 * 是否已开启身份验证 0-未开启 1-已开启
	 */
	private Integer isOpenAuthentication;
	
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
	 * 支付方式
	 */
	private String payType;
	
	/**
	 * 是否已全部赋码 0-否 1-是
	 */
	private Integer isAllCodes;
	
	/**
	 * 行业经验范围
	 */
	private String industryBusinessScope;
	
	/**
	 * 自选经验范围
	 */
	private String ownBusinessScope;
	
	/**
	 * 税费分类编码对应的经验范围
	 */
	private String taxcodeBusinessScope;
	
	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 纳税人类型 1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;
}
