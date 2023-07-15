package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 企业资源使用记录
 * 
 * @Date: 2019年12月10日 13:42:03 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_resources_use_record")
public class CompanyResourcesUseRecordEntity implements Serializable {
	
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
	 * 状态 0-不可用 1-可用
	 */
	private Integer status;

	/**
	 * 是否通知 0-未通知 1-24小时通知 2-72小时通知
	 */
	private Integer isNotice;
	
	/**
	 * 审核状态 0-待审核 1-已审核 2-审核不通过 3-已撤销
	 */
	private Integer auditStatus;
	
	/**
	 * 审核人
	 */
	private Long auditUser;
	
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
	 * 用途描述
	 */
	private String useDesc;

	/**
	 * 图片附件
	 */
	private String imgsAddr;

	/**
	 * 审核描述
	 */
	private String auditDesc;
}
