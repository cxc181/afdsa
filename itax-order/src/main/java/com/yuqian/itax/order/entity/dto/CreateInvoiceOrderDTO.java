package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建开票订单
 * 
 * @Date: 2019年12月07日 20:05:12 
 * @author yejian
 */
@Getter
@Setter
public class CreateInvoiceOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;


	/**
	 * 企业id
	 */
	@NotNull(message="企业id不能为空")
	@ApiModelProperty(value = "企业id", required = true)
	private Long companyId;
	
	/**
	 * 开票金额
	 */
	@NotNull(message="开票金额不能为空")
	@ApiModelProperty(value = "开票金额", required = true)
	private Long invoiceAmount;

	/**
	 * 开票类型 1-增值税普通发票 2-增值税专用发票
	 */
	@NotNull(message="开票类型不能为空")
	@Min(value = 1, message = "开票类型有误")
	@Max(value = 2, message = "开票类型有误")
	@ApiModelProperty(value = "开票类型：1->增值税普通发票；2-增值税专用发票", required = true)
	private Integer invoiceType;

	/**
	 * 发票类型 1-纸质发票 2-电子发票
	 */
	@NotNull(message="发票类型不能为空")
	@Min(value = 1, message = "发票类型有误")
	@Max(value = 2, message = "发票类型有误")
	private Integer invoiceWay;

	/**
	 * 开票类型名称
	 */
	@NotBlank(message="开票类型名称不能为空")
	@ApiModelProperty(value = "开票类型名称", required = true)
	private String invoiceTypeName;

	/**
	 * 发票抬头公司
	 */
	@NotBlank(message="发票抬头公司不能为空")
	@ApiModelProperty(value = "发票抬头公司名称", required = true)
	private String companyName;
	
	/**
	 * 发票抬头公司地址
	 */
	@ApiModelProperty(value = "发票抬头公司地址", required = true)
	private String companyAddress;
	
	/**
	 * 发票抬头税号
	 */
	@NotBlank(message="发票抬头税号不能为空")
	@Pattern(regexp = "^[A-Z0-9]{15}$|^[A-Z0-9]{18}$|^[A-Z0-9]{20}$", message = "税号格式不正确")
	@ApiModelProperty(value = "发票抬头税号", required = true)
	private String ein;
	
	/**
	 * 发票抬头电话号码
	 */
	@ApiModelProperty(value = "发票抬头电话号码", required = true)
	private String phone;
	
	/**
	 * 开户银行
	 */
	@Size(max = 64, message = "发票抬头开户银行不能超过64位字符")
	@ApiModelProperty(value = "发票抬头开户银行", required = true)
	private String bankName;
	
	/**
	 * 发票抬头银行账号
	 */
	@Size(max = 32, message = "发票抬头银行账号不能超过32位字符")
	@ApiModelProperty(value = "发票抬头银行账号", required = true)
	private String bankNumber;
	
	/**
	 * 收件人
	 */
	@ApiModelProperty(value = "收件人", required = true)
	private String recipient;
	
	/**
	 * 发票抬头联系电话
	 */
	@ApiModelProperty(value = "收件人联系电话", required = true)
	private String recipientPhone;
	
	/**
	 * 发票抬头收件地址
	 */
	@ApiModelProperty(value = "收件人收件地址", required = true)
	private String recipientAddress;

	/**
	 * 发票抬头省编码
	 */
	@ApiModelProperty(value = "发票抬头省编码", required = true)
	private String provinceCode;

	/**
	 * 发票抬头省名称
	 */
	@ApiModelProperty(value = "发票抬头省名称", required = true)
	private String provinceName;

	/**
	 * 发票抬头市编码
	 */
	@ApiModelProperty(value = "发票抬头市编码", required = true)
	private String cityCode;

	/**
	 * 发票抬头市名称
	 */
	@ApiModelProperty(value = "发票抬头市名称", required = true)
	private String cityName;

	/**
	 * 发票抬头区编码
	 */
	@ApiModelProperty(value = "发票抬头区编码", required = true)
	private String districtCode;

	/**
	 * 发票抬头区名称
	 */
	@ApiModelProperty(value = "发票抬头区名称", required = true)
	private String districtName;

	/**
	 * 钱包类型 1-消费钱包 2-佣金钱包
	 */
	@ApiModelProperty(value = "钱包类型 1-消费钱包 2-佣金钱包")
	private Integer walletType;

	/**
	 * 开票方式 1-自助开票 2-集团代开 3-佣金开票
	 */
	@ApiModelProperty(value = "开票方式 1-自助开票 2-集团代开 3-佣金开票")
	private Integer createWay;
	/**
	 * 收票邮箱
	 */
	private String email;
	/**
	 * 是否是前端小程序
	 */
	private boolean isApi = true;

	/**
	 * 开票类目id
	 */
	@NotNull(message="开票类目id不能为空")
	@ApiModelProperty(value = "开票类目id", required = true)
	private Long categoryId;

	/**
	 * 开票类目名称
	 */
	@NotBlank(message="开票类目名称不能为空")
	@ApiModelProperty(value = "开票类目名称", required = true)
	private String categoryName;

	/**
	 * 增值税税率
	 */
	@ApiModelProperty(value = "增值税税率", required = true)
	private BigDecimal vatRate;

	/**
	 * 商品名称
	 */
	@Size(max = 20, message = "商品名称不能超过20位字符")
	@ApiModelProperty(value = "商品名称")
	private String goodsName;
	/**
	 * 发票备注
	 */
	@Size(max = 200, message = "发票备注不能超过200位字符")
	@ApiModelProperty(value = "发票备注")
	private String invoiceRemark;
	/**
	 *支付类型 1-线上支付 2-线下支付
	 */
	private Integer payType=1;


}
