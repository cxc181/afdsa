package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 企业资源所在地历史记录
 * 
 * @Date: 2020年02月13日 15:35:51 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_resources_address_history")
public class CompanyResourcesAddressHistoryEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照 
	 */
	private Integer resourcesType;
	
	/**
	 * 变动前所在地
	 */
	private String updateBefore;
	
	/**
	 * 变动后所在地
	 */
	private String updateAfter;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
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
