package com.yuqian.itax.agent.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * oem机构开票信息
 * 
 * @Date: 2020年06月23日 09:29:36 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_invoice_info_by_oem")
public class InvoiceInfoByOemEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 公司名称
	 */
	private String companyName;
	
	/**
	 * 公司地址
	 */
	private String companyAddress;
	
	/**
	 * 税号
	 */
	private String ein;
	
	/**
	 * 电话号码
	 */
	private String phone;
	
	/**
	 * 注册地址
	 */
	private String registAddress;
	
	/**
	 * 开户银行
	 */
	private String bankName;
	
	/**
	 * 银行账号
	 */
	private String bankNumber;
	
	/**
	 * 收件人
	 */
	private String recipient;
	
	/**
	 * 联系电话
	 */
	private String recipientPhone;
	
	/**
	 * 详细地址
	 */
	private String recipientAddress;
	
	/**
	 * 状态  1-可用 0-不可用
	 */
	private Integer status;
	
	/**
	 * 省编码
	 */
	private String provinceCode;
	
	/**
	 * 省名称
	 */
	private String provinceName;
	
	/**
	 * 市编码
	 */
	private String cityCode;
	
	/**
	 * 市名称
	 */
	private String cityName;
	
	/**
	 * 区编码
	 */
	private String districtCode;
	
	/**
	 * 区名称
	 */
	private String districtName;
	
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
	 * 收票邮箱
	 */
	private String email;

	/**
	 * 托管状态 0-未托管 1-已托管
	 */
	private Integer hostingStatus;

	/**
	 * 税务盘类型 1-ukey 2-税控盘
	 */
	private Integer taxDiscType;

	/**
	 * 税务盘编号
	 */
	private String taxDiscCode;

	/**
	 * 票面金额类型 1-1w 2-10w 3-100w
	 */
	private Integer faceAmountType;

	/**
	 * 票面金额(分)
	 */
	private Long faceAmount;

	/**
	 * 通道方 1-百旺
	 */
	private Integer channel;

	/**
	 * 是否立即开票 0-否 1-是
	 */
	private Integer isImmediatelyInvoice;

	/**
	 * 消费发票增值税率
	 */
	private BigDecimal vatRate;
}
