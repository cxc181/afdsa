package com.yuqian.itax.user.entity.dto;

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
public class UpdateRegOrderDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 经营者姓名
	 */
	private String operatorName;

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
	 * 身份证地址
	 */
	private String idCardAddr;

	/**
	 * 经办人账号
	 */
	private String agentAccount;

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

}
