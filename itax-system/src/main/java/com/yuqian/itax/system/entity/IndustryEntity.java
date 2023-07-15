package com.yuqian.itax.system.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 行业
 * 
 * @Date: 2019年12月08日 20:37:33 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_industry")
public class IndustryEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 注册名称示例
	 */
	private String industryName;

	/**
	 * 行业名称
	 */
	private String exampleName;
	/**
	 * 发票样例
	 */
	private String exampleInvoice;
	
	/**
	 * 上级行业id
	 */
	private Long parentIndustryId;

	/**
	 * 园区ID
	 */
	private Long parkId;

	/**
	 * 企业类型1-个体开户 2-个独开户 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
	
	/**
	 * 状态  0-不可用 1-可用
	 */
	private Integer status;
	
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
	 * 其他说明
	 */
	private String orderDesc;

	
}
