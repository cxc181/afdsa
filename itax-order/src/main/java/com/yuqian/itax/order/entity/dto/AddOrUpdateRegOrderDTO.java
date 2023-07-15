package com.yuqian.itax.order.entity.dto;

import com.yuqian.itax.order.entity.RegisterOrderGoodsDetailRelaEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 16:11
 *  @Description: 工商注册订单新增/编辑接收实体类
 */
@Getter
@Setter
public class AddOrUpdateRegOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	@NotNull(message = "请传入园区ID")
	@ApiModelProperty(value = "园区ID", required = true)
	private Long parkId;// 园区ID

	/**
	 * 产品id
	 */
	@NotNull(message = "请传入产品ID")
	@ApiModelProperty(value = "产品ID", required = true)
	private Long productId;// 产品ID

	/**
	 * 行业类型id
	 */
	@NotNull(message = "请输入行业类型ID")
	@ApiModelProperty(value = "行业类型id",required = true)
	private Long industryId;

	/**
	 * 经营范围(等于行业经营范围+自选经营范围)
	 */
	private String businessScope;

	/**
	 * 自选经验范围
	 */
	private String ownBusinessScope;

	/**
	 * 行业经营范围
	 */
	@NotBlank(message = "行业经营范围不能为空")
	private String industryBusinessScope;

	/**
	 * 注册名称
	 */
	@NotBlank(message = "请输入注册名称")
	@ApiModelProperty(value = "注册名称",required = true)
	private String registeredName;

	/**
	 * 字号
	 */
	@Pattern(regexp = "^[\u4e00-\u9fa5]{2,6}", message = "字号格式不正确，请输入2-6个汉字")
	@NotBlank(message = "请输入字号")
	private String shopName;

	/**
	 * 备选字号1
	 */
//	@Pattern(regexp = "^[\u4e00-\u9fa5]{2,6}", message = "备选字号1格式不正确，请输入2-6个汉字")
//	@NotBlank(message = "请输入备选字号1")
	private String shopNameOne;

	/**
	 * 备选字号2
	 */
//	@Pattern(regexp = "^[\u4e00-\u9fa5]{2,6}", message = "备选字号2格式不正确，请输入2-6个汉字")
//	@NotBlank(message = "请输入备选字号2")
	private String shopNameTwo;

	/**
	 * 是否自动生成（企业信息） 0-否 1-是
	 */
	private Integer isAutoCreate = 0;

	/**
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任
	 */
	@NotNull(message = "企业类型不能为空")
	private Integer companyType;

	/**
	 * 注册资本（万元）
	 */
	private BigDecimal registeredCapital;

	/**
	 * 纳税人类型 1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType = 1;

	/**
	 * 订单号（携带订单号时默认为编辑订单操作）
	 */
	private String orderNo;

	/**
	 * 示例名称
	 */
	@ApiModelProperty(value = "示例名称",required = true)
	private String exampleName;

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
	 * 备注
	 */
	private String remark;
	
	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 操作来源
	 */
	private Integer sourceType;
}
