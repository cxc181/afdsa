package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/14 15:08
 *  @Description: 企业资源使用记录查询参数Bean
 */
@Getter
@Setter
public class CompanyResourcesUseRecordQuery extends BaseQuery implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 审核人ID
	 */
	private Long userId;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 资源类型
	 */
	private Integer resourcesType;
	
	/**
	 * 状态 0-不可用 1-可用
	 */
	private Integer status;
	
	/**
	 * 审核状态 0-待审核 1-已审核
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

}
