package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 发票抬头
 * 
 * @Date: 2019年12月07日 20:48:40 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_head")
public class InvoiceHeadEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(value = "主键id")
	private Long id;
	
	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private Long memberId;
	
	/**
	 * 公司名称
	 */
	@ApiModelProperty(value = "公司名称")
	private String companyName;
	
	/**
	 * 公司地址
	 */
	@ApiModelProperty(value = "公司地址")
	private String companyAddress;
	
	/**
	 * 税号
	 */
	@ApiModelProperty(value = "税号")
	private String ein;
	
	/**
	 * 电话号码
	 */
	@ApiModelProperty(value = "电话号码")
	private String phone;
	
	/**
	 * 开户银行
	 */
	@ApiModelProperty(value = "开户银行")
	private String bankName;
	
	/**
	 * 银行账号
	 */
	@ApiModelProperty(value = "银行账号")
	private String bankNumber;
	
	/**
	 * 状态 0-不可用 1-可用
	 */
	@ApiModelProperty(value = "状态：0->不可用；1-可用")
	private Integer status;
	
	/**
	 * 添加时间
	 */
	@ApiModelProperty(value = "添加时间")
	private Date addTime;
	
	/**
	 * 添加人
	 */
	@ApiModelProperty(value = "添加人")
	private String addUser;
	
	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updateUser;
	
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;

}
