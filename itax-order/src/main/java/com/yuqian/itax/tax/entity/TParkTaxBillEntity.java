package com.yuqian.itax.tax.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区税单查询
 * 
 * @Date: 2020年12月03日 10:36:14 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_park_tax_bill")
public class TParkTaxBillEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 园区税单id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
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
	private Integer invoiceCompanyNumber;
	
	/**
	 * 已上传企业
	 */
	private Integer uploadingCompanyNumber;
	
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
	 * 税单状态 0-待确认 1-解析中 2-待上传 3-已确认
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
	 * 
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 应上传增值税凭证企业
	 */
	private Integer shouldUploadVatVouchersCompanyNumber;
	/**
	 * 应上传个税凭证企业
	 */
	private Integer shouldUploadIitVouchersCompanyNumber;
	/**
	 * 已上传增值税证企业
	 */
	private Integer alreadyUploadVatVouchersCompanyNumber;
	/**
	 * 已上传个税凭证企业
	 */
	private Integer alreadyUploadIitVouchersCompanyNumber;
	/**
	 * 凭证状态 0-未上传 1-解析中 2-已上传
	 */
	private Integer vouchersStatus;
	/**
	 * 作废/红冲企业
	 */
	private Integer cancellationCompany;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private Integer incomeLevyType;
}
