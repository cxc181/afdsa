package com.yuqian.itax.tax.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 税单完税凭证解析记录表
 * 
 * @Date: 2020年12月25日 11:34:04 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_tax_bill_credentials_record")
public class TaxBillCredentialsRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 企业税单id
	 */
	private Long companyTaxBillId;
	
	/**
	 * 税号
	 */
	private String ein;
	
	/**
	 * 园区税单id
	 */
	private Long parkTaxBillId;
	
	/**
	 * 批次号
	 */
	private String batchNumber;
	
	/**
	 * 个税凭证地址
	 */
	private String iitVoucherPic;
	
	/**
	 * 增值税凭证地址
	 */
	private String vatVoucherPic;
	
	/**
	 * 解析状态 0-解析成功 1-解析失败
	 */
	private Integer status;
	
	/**
	 * 失败原因
	 */
	private String resultMsg;
	
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
	 * 罚款凭证地址
	 */
	private String ticketVoucherPic;
}
