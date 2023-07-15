package com.yuqian.itax.order.entity.dto;

import com.yuqian.itax.order.entity.RegisterOrderGoodsDetailRelaEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 16:11
 *  @Description: 工商注册订单创建接收实体类
 */
@Getter
@Setter
public class RegisterOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	@NotBlank(message = "请输入机构编码")
	@ApiModelProperty(value = "机构编码",required = true)
	private String oemCode;

	private String oemName;// 结构名称

	private Long prodAmount;// 产品金额

	private Integer prodType;// 产品类型
	
	/**
	 * 经营者姓名
	 */
	@ApiModelProperty(value = "经营者姓名",required = true)
	private String operatorName;
	
	/**
	 * 字号
	 */
	@Pattern(regexp = "^[\u4e00-\u9fa5]{2,6}", message = "字号格式不正确，请输入2-6个汉字")
	@NotBlank(message = "请输入字号")
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
	@NotNull(message = "请输入行业类型ID")
	@ApiModelProperty(value = "行业类型id",required = true)
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
	@ApiModelProperty(value = "电子邮箱",required = true)
	private String email;
	
	/**
	 * 身份证正面
	 */
	@NotBlank(message = "请输入身份证正面照地址")
	@ApiModelProperty(value = "身份证正面",required = true)
	private String idCardFront;
	
	/**
	 * 身份证反面
	 */
	@NotBlank(message = "请输入身份证反面照地址")
	@ApiModelProperty(value = "身份证反面",required = true)
	private String idCardReverse;
	
	/**
	 * 身份证号码
	 */
	@NotBlank(message = "请输入身份证号码")
	@ApiModelProperty(value = "身份证号码",required = true)
	private String idCardNumber;

	/**
	 * 身份证有效期
	 */
	@NotBlank(message = "身份证有效期不能为空")
	@ApiModelProperty(value = "身份证有效期",required = true)
	private String expireDate;

	/**
	 * 身份证地址
	 */
	@NotBlank(message = "身份证地址不能为空")
	@ApiModelProperty(value = "身份证地址",required = true)
	private String idCardAddr;
	
	/**
	 * 核定税种
	 */
	@NotBlank(message = "请选择核定税种")
	@ApiModelProperty(value = "核定税种",required = true)
	private String ratifyTax;
	
	/**
	 * 经营范围
	 */
	@NotBlank(message = "请选择经营范围")
	@ApiModelProperty(value = "经营范围",required = true)
	private String businessScope;
	
	/**
	 * 注册名称
	 */
	@NotBlank(message = "请输入注册名称")
	@ApiModelProperty(value = "注册名称",required = true)
	private String registeredName;

	/**
	 * 示例名称
	 */
	@ApiModelProperty(value = "示例名称",required = true)
	private String exampleName;

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
	@ApiModelProperty(value = "订单金额")
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
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任',
	 */
	private Integer companyType;

	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	@NotNull(message = "请输入办理标识")
	private Integer isOther;

	/**
	 * 短视频地址
	 */
	private String videoAddr;

	@NotNull(message = "请传入产品ID")
	@ApiModelProperty(value = "产品ID", required = true)
	private Long productId;// 产品ID

	private String productName;// 产品名称

	@NotNull(message = "请传入园区ID")
	@ApiModelProperty(value = "园区ID", required = true)
	private Long parkId;// 园区ID

	/**
	 * 操作小程序来源 1-微信小程序 2-支付宝小程序
	 */
	@ApiModelProperty(value = "操作小程序来源 1-微信小程序 2-支付宝小程序", required = true)
	private Integer sourceType;

	/**
	 * 是否自费 1-自费 2-承担方
	 */
	private Integer isSelfPaying;

	/**
	 * 费用承担方
	 */
	private String payerName;

	/**
	 * 支付方式 在线支付_1,线下结算_2
	 */
	private Integer payType;

	/**
	 * 是否全部赋码
	 */
	private Integer isAllCodes;

	/**
	 * 税费分类编码对应的经验范围
	 */
	private String taxCodeBusinessScope;

	/**
	 * 自选经验范围
	 */
	private String ownBusinessScope;

	/**
	 * 行业经营范围
	 */
	private String industryBusinessScope;

	/**
	 * 商品编码及商品名称列表
	 */
	private List<RegisterOrderGoodsDetailRelaEntity> merchandises;

	/**
	 * 业务来源单号
	 */
	private String externalOrderNo;

	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 纳税人类型 1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 是否需要补充字号
	 */
	private int isSupplyShopName;
}
