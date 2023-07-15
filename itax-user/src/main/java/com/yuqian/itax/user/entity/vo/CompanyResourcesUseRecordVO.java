package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/14 14:14
 *  @Description: 企业资源使用记录前端展示bean
 */
@Getter
@Setter
public class CompanyResourcesUseRecordVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 企业类型
	 */
	private Integer companyType;

	/**
	 * 企业类型名称
	 */
	private String companyTypeName;
	
	/**
	 * 资源类型
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
	 * 审核状态名称
	 */
	private String auditStatusName;

	/**
	 * 用途描述
	 */
	private String useDesc;

	/**
	 * 审核人
	 */
	private Long auditUser;

	/**
	 * 申请人
	 */
	private String addUser;
	
	/**
	 * 添加时间
	 */
	private Date addTime;

	/**
	 * 附件地址
	 */
	private String imgsAddr;

}
