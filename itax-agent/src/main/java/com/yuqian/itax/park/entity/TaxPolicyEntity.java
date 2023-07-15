package com.yuqian.itax.park.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 税费政策
 * 
 * @Date: 2019年12月12日 18:38:59 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_tax_policy")
public class TaxPolicyEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 企业类型 1-个体工商 2-个体独资 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
	
	/**
	 * 增值税减免额度
	 */
	private Long vatBreaksAmount;
	
	/**
	 * 增值税减免周期 1-按月 2-按季度
	 */
	private Integer vatBreaksCycle;
	
	/**
	 * 个人所得税减免额度
	 */
	private Long incomeTaxBreaksAmount;
	
	/**
	 * 个人所得税减免周期 1-按月 2-按季度
	 */
	private Integer incomeTaxBreaksCycle;
	/**
	 * 附加税减免额度
	 */
	private Long surchargeBreaksAmount;

	/**
	 * 附加税减免周期 1-按月 2-按季度
	 */
	private Integer surchargeBreaksCycle;
	
	/**
	 * 办理要求
	 */
	private String transactRequire;

	/**
	 * 视频认证阅读内容
	 */
	private String readContent;
	
	/**
	 * 状态 0-待上架 1-已上架 2-已下架 3-已暂停
	 */
	private Integer status;
	
	/**
	 * 年度开票总额
	 */
	private Long totalInvoiceAmount;

	/**
	 * 季开票额度
	 */
	private Long quarterInvoiceAmount;

	/**
	 * 月开票额度
	 */
	private Long monthInvoiceAmount;
	
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
	 * 政策文件链接
	 */
	private String policyFileUrl;

	/**
	 * 计税方式（1：预缴征收率，2：核定应税所得率）
	 */
	private Integer levyWay;
	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private Integer incomeLevyType;

	/**
	 * 纳税人类型  1-小规模纳税人 2-一般纳税人
	 */
	private Integer taxpayerType;

	/**
	 * 园区政策说明
	 */
	private String parkPolicyDesc;

	/**
	 * 特殊事项说明
	 */
	private String specialConsiderations;

	/**
	 * 印花税申报周期 1-按月 2-按季度
	 */
	private Integer stampDutyBreaksCycle;

	/**
	 * 印花税是否减半 0-否 1-是
	 */
	private Integer isStampDutyHalved;

	/**
	 * 水利建设基金申报周期 1-按月 2-按季度
	 */
	private Integer waterConservancyFundBreaksCycle;

	/**
	 * 水利建设基金是否减半 0-否 1-是
	 */
	private Integer isWaterConservancyFundHalved;

	/**
	 * 应纳税所得额减免
	 */
	private Long incomeTaxableIncomeBreaks;

	/**
	 * 所得税税收减免比例
	 */
	private BigDecimal incomeTaxReliefRatio;
}
