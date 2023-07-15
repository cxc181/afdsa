package com.yuqian.itax.tax.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区上传税单记录表
 * 
 * @Date: 2020年12月03日 10:36:31 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_tax_bill_file_record")
public class ParkTaxBillFileRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 园区上传税单记录表
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer taxBillSeasonal;

	private Integer taxBillYear;
	/**
	 * 园区税单id
	 */
	private Long parkTaxBill;

	/**
	 *  企业id
	 */
	private Long companyId;

	
	/**
	 * 企业名称
	 */
	private String companyName;
	
	/**
	 * 税号
	 */
	private String ein;
	
	/**
	 * 本季开票金额
	 */
	private Long seasonInvoiceAmount;
	
	/**
	 * 总税费
	 */
	private Long totalTaxAmount;
	
	/**
	 * 增值税
	 */
	private Long vat;
	
	/**
	 * 附加税
	 */
	private Long additional;
	
	/**
	 * 所得税
	 */
	private Long income;
	
	/**
	 * 附件地址
	 */
	private String fileUrl;
	/**
	 * 失败原因
	 */
	private String failed;
	/**
	 * 解析状态 0-解析成功 1-解析失败
	 */
	private Integer status;

	
	/**
	 * 创建时间
	 */
	private Date addTime;
	
	/**
	 * 创建人
	 */
	private String addUser;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 作废/红冲金额
	 */
	private BigDecimal cancellationAmount;
}
