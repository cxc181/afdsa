package com.yuqian.itax.user.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *  @Author: Kaven
 *  @Date: 2019/12/14 14:14
 *  @Description: 企业资源使用记录前端参数接收bean
 */
@Getter
@Setter
public class CompanyResourcesUseRecordDTO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@NotNull(message = "主键id不能为空")
	@ApiModelProperty(value = "主键id",required = true)
	private Long id;

	/**
	 * 审核状态 0-待审核 1-已审核 2-审核不通过
	 */
	@NotNull(message = "审核状态不能为空")
	@ApiModelProperty(value = "审核状态1-审核通过 2-审核不通过",required = true)
	private Integer auditStatus;

	/**
	 * 审核人
	 */
	private Long auditUser;

	/**
	 * 审核不通过理由
	 */
	@ApiModelProperty(value = "审核不通过理由，不通过时必传")
	private String auditDesc;

	/**
	 * 图片附件
	 */
	@ApiModelProperty(value = "图片附件，多个用逗号分隔")
	private String imgAddr;

	/**
	 * 添加时间
	 */
	private Date addTime;

}
