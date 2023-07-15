package com.yuqian.itax.order.entity.dto;

import com.yuqian.itax.order.entity.RegisterOrderGoodsDetailRelaEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/10 16:11
 *  @Description: 工商注册订单创建接收实体类(接入方)
 */
@Getter
@Setter
public class AccessPartyRegisterOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码",required = true)
	private String oemCode;
	
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
	 * 行业类型id
	 */
	@NotNull(message = "请输入行业类型ID")
	@ApiModelProperty(value = "行业类型id",required = true)
	private Long industryId;

	/**
	 * 核定税种
	 */
	@ApiModelProperty(value = "核定税种",required = true)
	private String ratifyTax;
	
	/**
	 * 经营范围
	 */
	@ApiModelProperty(value = "经营范围",required = true)
	private String businessScope;
	
	/**
	 * 注册名称
	 */
	@ApiModelProperty(value = "注册名称",required = true)
	private String registeredName;

	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	private Integer isOther;

	/**
	 * 产品ID
	 */
	@ApiModelProperty(value = "产品ID", required = true)
	private Long productId;// 产品ID

	/**
	 * 园区ID
	 */
	@NotNull(message = "请传入园区ID")
	@ApiModelProperty(value = "园区ID", required = true)
	private Long parkId;// 园区ID

	/**
	 * 是否自费 1-自费 2-承担方
	 */
	@NotNull(message = "请传入是否自费 1-自费 2-承担方")
	@ApiModelProperty(value = "是否自费 1-自费 2-承担方", required = true)
	private Integer isSelfPaying;

	/**
	 * 费用承担方
	 */
	private String payerName;

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
	@NotBlank(message = "行业经营范围不能为空")
	private String industryBusinessScope;

	/**
	 * 商品编码及商品名称列表
	 */
	private List<RegisterOrderGoodsDetailRelaEntity> merchandises;

	/**
	 * 业务来源单号
	 */
	@NotBlank(message = "业务来源单号不能为空")
	private String externalOrderNo;
}
