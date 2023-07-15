package com.yuqian.itax.order.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/11 19:40
 *  @Description: 工商注册订单展示实体类
 */
@Getter
@Setter
public class RegisterOrderVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String orderNo;

    /**
     * 订单状态
     */
	@ApiModelProperty(value = "订单状态")
    private String orderStatus;

	/**
	 * 订单状态名称
	 */
	@ApiModelProperty(value = "订单状态名称")
	private String orderStatusName;

	/**
	 * 产品ID
	 */
	@ApiModelProperty(value = "产品ID")
	private Long productId;

	/**
	 * 产品名称
	 */
	@ApiModelProperty(value = "产品名称")
	private String prodName;
	
	/**
	 * 机构编码
	 */
	@ApiModelProperty(value = "机构编码")
	private String oemCode;
	
	/**
	 * 经营者姓名
	 */
	@ApiModelProperty(value = "经营者姓名")
	private String operatorName;

	/**
	 * 行业类型id
	 */
	@ApiModelProperty(value = "行业类型id")
	private Long industryId;

	/**
	 * 园区id
	 */
	@ApiModelProperty(value = "园区id")
	private Long parkId;

	/**
	 * 园区编码
	 */
	@ApiModelProperty(value = "园区编码")
	private String parkCode;

	/**
	 * 流程标记（1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程）
	 */
	private Integer processMark;

	/**
	 * 企业类型
	 */
	@ApiModelProperty(value = "企业类型")
	private Integer companyType;

	/**
	 * 行业类型名称
	 */
	@ApiModelProperty(value = "行业类型名称")
	private String industryName;

	/**
	 * 注册名称
	 */
	@ApiModelProperty(value = "注册名称")
	private String registeredName;
	
	/**
	 * 订单金额
	 */
	@ApiModelProperty(value = "订单金额")
	private Long orderAmount;

	/**
	 * 订单支付金额
	 */
	@ApiModelProperty(value = "订单支付金额")
	private Long payAmount;

	/**
	 * 联系电话
	 */
	@ApiModelProperty(value = "联系电话")
	private String contactPhone;

	/**
	 * 专属客户经理
	 */
	@ApiModelProperty(value = "专属客户经理")
	private String customerServicePhone;

	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;

	/**
	 * 是否已开启身份验证 0-未开启 1-已开启
	 */
	private Integer isOpenAuthentication;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 驳回项，多个项之间用逗号分割 1-字号 2-身份证 3-视频
	 */
	private String rejectedItem;

	/**
	 * 优惠券发放id
	 */
	private Long couponsIssueId;

	/**
	 * 优惠券金额
	 */
	private Long couponsAmount;

	/**
	 * 是否隐藏“取消”按钮
	 */
	private Integer isHideCancel;

	/**
	 * 所属地区 1-江西
	 */
	private Integer affiliatingArea;
}
