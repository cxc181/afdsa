package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 银行卡bin
 * 
 * @Date: 2019年12月14日 11:39:30 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_bank_bin")
public class BankBinEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 发卡行代码
	 */
	private String bankCode;
	
	/**
	 * 银行名称
	 */
	private String bankName;
	
	/**
	 * BIN长度
	 */
	private Integer binLen;
	
	/**
	 * 行银卡类型
	 */
	private Integer cardType;
	
	/**
	 * 卡种名称
	 */
	private String cardName;
	
	/**
	 * 卡号长度
	 */
	private Integer cardLen;
	
	/**
	 * 卡BIN
	 */
	private String bin;
	
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
