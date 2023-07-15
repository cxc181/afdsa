package com.yuqian.itax.group.entity;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 集团打款流水解析记录表
 * 
 * @Date: 2020年03月04日 09:26:23 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_group_payment_analysis_record")
public class GroupPaymentAnalysisRecordEntity implements Serializable {
	
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
	 * 集团开票订单号
	 */
	private String groupOrderNo;
	
	/**
	 * 付款企业名称
	 */
	@Excel(name = "付款企业名称", width = 20)
	private String paymentGroupName;
	
	/**
	 * 付款银行名称
	 */
	@Excel(name = "付款银行名称", width = 20)
	private String paymentBankName;
	
	/**
	 * 付款银行账号
	 */
	@Excel(name = "付款银行账号", width = 20)
	private String paymentBankAccount;
	
	/**
	 * 付款时间
	 */
	@Excel(name = "付款时间", width = 20)
	private String paymentTime;
	
	/**
	 * 付款金额(元)
	 */
	@Excel(name = "付款金额（元）", width = 20)
	private String paymentAmount;
	
	/**
	 * 本次开票金额(元）
	 */
	@Excel(name = "本次开票金额（元）", width = 20)
	private String invoiceAmount;
	
	/**
	 * 收款人姓名
	 */
	@Excel(name = "收款人姓名", width = 20)
	private String payeeName;
	
	/**
	 * 收款银行名称
	 */
	@Excel(name = "收款银行名称", width = 20)
	private String payeeBankName;
	
	/**
	 * 收款银行卡号
	 */
	@Excel(name = "收款银行卡号", width = 20)
	private String payeeBankAccount;
	
	/**
	 * 收款人身份证号
	 */
	@Excel(name = "收款人身份证号", width = 20)
	private String payeeIdcard;

	/**
	 * 个体户名称
	 */
	@Excel(name = "个体户名称", width = 20)
	private String invoiceCompanyName;

	/**
	 * 解析结果 0-解析中 1-解析成功 2-解析失败
	 */
	@Excel(name = "解析结果", replace = { "解析中_0","解析成功_1","解析失败_2" }, width = 15)
	private Integer analysisResult;
	
	/**
	 * 失败原因
	 */
	@Excel(name = "失败原因", width = 20)
	private String errorResult;
	
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
	
	
}
