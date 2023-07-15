package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description 工商注册订单创建接收实体类（外部调用）
 * @Author  Kaven
 * @Date   2020/7/15 15:04
*/
@Getter
@Setter
public class OuterRegOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 外部订单号
	 */
	@Size(max = 32,message = "外部订单号最大不能超过32个字符")
	@NotBlank(message = "外部订单号不能为空")
	private String externalOrderNo;

	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码",required = true)
	private String oemCode;

	/**
	 * 经营者姓名
	 */
	@NotBlank(message = "经营者姓名不能为空")
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
	 * 行业类型id
	 */
	@NotNull(message = "请输入行业类型ID")
	@ApiModelProperty(value = "行业类型id",required = true)
	private Long industryId;
	
	/**
	 * 经营者手机号
	 */
	@Pattern(regexp = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$", message = "经营者手机号不合规，请检查")
	@NotBlank(message = "经营者手机号不能为空")
	@ApiModelProperty(value = "经营者手机号",required = true)
	private String regPhone;
	
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
	 * 身份证地址
	 */
	@NotBlank(message = "身份证地址不能为空")
	@ApiModelProperty(value = "身份证地址",required = true)
	private String idCardAddr;

	/**
	 * 身份证有效期
	 */
	@NotBlank(message = "身份证有效期不能为空")
	@ApiModelProperty(value = "身份证有效期",required = true)
	private String expireDate;
	
	/**
	 * 签名单
	 */
	@NotNull(message = "签名地址不能为空")
	private String signImg;
	
	/**
	 * 企业类型 1-个体开户 2-个独开户 3-有限合伙 4-有限责任',
	 */
	private Integer companyType;

	/**
	 * 短视频地址
	 */
	private String videoAddr;

	/**
	 * 园区ID
	 */
	private Long parkId;

	/**
	 * 产品ID
	 */
	private Long productId;

	@NotBlank(message = "请传入园区编码")
	@ApiModelProperty(value = "园区编码",required = true)
	private String parkCode;// 园区编码

	/**
	 * 产品名称
	 */
	private String productName;

	/**
	 * 示例名称
	 */
	private String exampleName;

	/**
	 * OEM机构名称
	 */
	private String oemName;

	/**
	 * 产品金额
	 */
	private Long prodAmount;

	/**
	 * 产品类型
	 */
	private Integer prodType;

	/**
	 * 是否为他人办理 0-本人办理 1-为他人办理
	 */
	private Integer isOther;

	private Long orderAmount;// 订单金额

	/**
	 * 注册名称
	 */
	@ApiModelProperty(value = "注册名称",required = true)
	private String registeredName;

	private String parkCity;// 园区所在市

	private Date addTime;// 订单创建时间

}
