package com.yuqian.itax.tax.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区税单变更表
 * 
 * @Date: 2021年03月16日 14:47:22 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_tax_bill_change")
public class ParkTaxBillChangeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 园区税单id
	 */
	private Long parkBillsId;
	
	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;
	
	/**
	 * 税款所属期-季度
	 */
	private Integer taxBillSeasonal;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 本期开票企业
	 */
	private Long invoiceCompanyNumber;
	
	/**
	 * 已上传企业
	 */
	private Long uploadingCompanyNumber;
	
	/**
	 * 已交税费
	 */
	private Long alreadyTaxMoney;
	
	/**
	 * 总应纳税所得额
	 */
	private Long shouldTaxMoney;
	
	/**
	 * 应退税费
	 */
	private Long recoverableTaxMoney;
	
	/**
	 * 应补税费
	 */
	private Long supplementTaxMoney;
	
	/**
	 * 应上传增值税凭证企业
	 */
	private Integer shouldUploadVatVouchersCompanyNumber;
	
	/**
	 * 应上传个税凭证企业
	 */
	private Integer shouldUploadIitVouchersCompanyNumber;
	
	/**
	 * 已上传增值税凭证企业
	 */
	private Integer alreadyUploadVatVouchersCompanyNumber;
	
	/**
	 * 已上传个税凭证企业
	 */
	private Integer alreadyUploadIitVouchersCompanyNumber;
	
	/**
	 * 凭证状态 0-未上传 1-解析中 2-已上传3-部分已上传
	 */
	private Integer vouchersStatus;
	
	/**
	 * 税单状态 0-待确认 1-解析中 2-待上传 3-已确认 4-待凭证上传 5-推送中
	 */
	private Integer taxBillStatus;
	
	/**
	 * 最新附件地址
	 */
	private String curFileUrl;
	
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
	 * 更新人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 作废/红冲企业
	 */
	private Integer cancellationCompany;
}
