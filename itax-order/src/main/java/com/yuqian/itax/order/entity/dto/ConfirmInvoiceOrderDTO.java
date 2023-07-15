package com.yuqian.itax.order.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 确认开票订单
 * 
 * @Date: 2019年12月07日 20:05:12 
 * @author yejian
 */
@Getter
@Setter
public class ConfirmInvoiceOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

    /**
     * 订单号
     */
	@NotBlank(message="订单号不能为空")
    @ApiModelProperty(value = "订单号", required = true)
    private String orderNo;


	/**
	 * 银行流水截图（多张逗号拼接）
	 */
	@ApiModelProperty(value = "银行流水截图（多张逗号拼接）")
	private String accountStatement;

	/**
	 * 是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票
	 */
	@ApiModelProperty(value = "是否先开票后补流水 0-先开票后补流水 1-先上传流水再开票")
	private Integer isAfterUploadBankWater;

	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

	/**
	 * 业务合同
	 */
	@ApiModelProperty(value = "业务合同")
	private String businessContractImgs;

	/**
	 * 补充说明
	 */
	private String supplementExplain;

	/**
	 *是否先开票后补交易成果 0-先开票后补交易成果 1-先上传交易成果再开票
	 */
	private Integer isAfterAchievement;
	/**
	 *成果状态 0-无需上传 1-成果前置 2-待上传 3-审核中 4-审核不通过 5-审核通过
	 */
	private Integer achievementStatus;
	/**
	 * 成果图片，多个图片之间用逗号分割
	 */
	private String achievementImgs;
	/**
	 * 成果视频
	 */
	private String achievementVideo;

	/**
	 * 开票方式 1-自助开票 2-集团代开 3-佣金开票
	 */
	@ApiModelProperty(value = "开票方式 1-自助开票 2-集团代开 3-佣金开票")
	private Integer createWay;

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
	 * 收票邮箱
	 */
	private String email;
}
