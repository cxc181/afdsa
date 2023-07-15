package com.yuqian.itax.product.entity.dto;

import com.yuqian.itax.agent.entity.dto.ProductagreementTemplateDTO;
import com.yuqian.itax.product.entity.ChargeStandardEntity;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 产品管理
 * 
 * @Date: 2019年12月07日 20:41:26 
 * @author 蒋匿
 */
@Getter
@Setter
public class ProductDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@NotNull(message="主键不能为空", groups={Update.class})
	private Long id;
	
	/**
	 * 机构编码
	 */
	@Size(max = 30, message = "机构编码不能超过30位", groups={Add.class})
	private String oemCode;

	/**
	 * 产品名称
	 */
	@NotBlank(message="产品名称不能为空", groups={Add.class, Update.class})
	@Size(min = 1, max = 30, message = "产品名称不能超过30位字符", groups={Add.class, Update.class})
	private String prodName;

	/**
	 * 产品编码
	 */
	@NotBlank(message="产品编码不能为空", groups={Add.class})
	@Pattern(regexp = "^[a-zA-Z0-9]{0,15}$", message = "产品编码只支持15位以内字母和数字", groups={Add.class})
	private String prodCode;
	
	/**
	 * 产品类型 1-个体开户 2-个独开户 3-有限合伙开户 4-有限责任开户  5-个体开票 6-个独开票 7-有限合伙开票 8-有限责任开票 9-黄金会员（废弃）
	 * 10-钻石会员 （废弃）11-个体注销 12-个独注销 13-有限合伙注销 14-有限责任注销  15-公户申请和托管 16-个体托管费续费 17-对公户年费续费
	 * 18-个独托管费续费 19-有限合伙托管费续费 20-有限责任开户托管费续费
	 */
	@NotNull(message="产品类型不能为空", groups={Add.class})
	@Min(value = 1, message = "产品类型有误", groups={Add.class})
	@Max(value = 20, message = "产品类型有误", groups={Add.class})
	private Integer prodType;
	
	/**
	 * 金额（元）
	 */
	private BigDecimal prodAmount;
	
	/**
	 * 金额名称（开户年费/会费）
	 */
	private String amountName;
	
	/**
	 * 费用方式 1-固定金额 2-比率
	 */
	private Integer amountWay;
	
	/**
	 * 产品简介
	 */
	@NotBlank(message="产品简介不能为空", groups={Add.class, Update.class})
	@Size(min = 1, max = 30, message = "产品简介不能超过30位字符", groups={Add.class, Update.class})
	private String prodDesc;

	/**
	 * 园区id
	 */
	private String parkIds;

	/**
	 * 开票服务费json格式
	 * {
	 *   "chargeMin": "123",  最小元
	 *   "chargeMax": "",     最大元
	 *   "chargeRate": "12"   费率
	 * }
	 */
	private String jsonStr;

	/**
	 * 操作时间
	 */
	private Date operationTime;
	
	/**
	 * 操作人
	 */
	private String operator;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 园区id集合
	 */
	private List<Long> parkIdList;

	/**
	 * 收费标准集合
	 */
	private List<ChargeStandardEntity> charges;

	/**
	 * 注销累计开票额度 (元)
	 */
	private BigDecimal cancelTotalLimit;

	/**
	 * 办理费（对公户独有）
	 */
	private BigDecimal processingFee;

	/**
	 * 园区单独定价
	 */
	private List<ProductAndParkDTO> productByParkList;


	/**
	 * 收费标准协议模板id
	 */
	private Long chargeStandardTemplateId;

	/**
	 * 注册流程&协议
	 */
	private List<ProductagreementTemplateDTO> agreementTemplateInfoList;

	/**
	 * 企业类型
	 */
	private Integer companyType;
}
