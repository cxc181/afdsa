package com.yuqian.itax.user.entity.dto;

import com.yuqian.itax.user.entity.CompanyResourcesUseRecordEntity;
import com.yuqian.itax.user.entity.MemberCompanyEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.user.enums.AuditStateEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 企业资源使用记录
 *
 * @Date: 2019年12月10日 13:42:03
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyResourcesUseRecordAdminDTO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 企业id
	 */
	@NotNull(message = "企业id不能为空")
	private Long companyId;

	/**
	 * 印章类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照
	 */
	@NotNull(message = "印章类型不能为空")
	@Min(value = 1, message = "印章类型有误")
	@Max(value = 10, message = "印章类型有误")
	private Integer resourcesType;

	/**
	 * 用途说明
	 */
	@NotBlank(message = "用途说明不能为空")
	@Size(max = 60, message = "用途说明不能超过60位字符")
	private String useDesc;

	/**
	 * 图片附件
	 */
	@NotBlank(message = "图片附件不能为空")
	@Size(max = 1000, message = "图片附件不能超过1000位字符")
	private String imgsAddr;

	public CompanyResourcesUseRecordEntity dtoToEntity(UserEntity userEntity, MemberCompanyEntity companyEntity) {
		CompanyResourcesUseRecordEntity entity = new CompanyResourcesUseRecordEntity();
		entity.setCompanyId(companyId);
		entity.setResourcesType(resourcesType);
		entity.setStatus(1);
		entity.setAuditUser(companyEntity.getMemberId());
		entity.setAuditStatus(AuditStateEnum.TO_APPROVE.getValue());
		entity.setImgsAddr(imgsAddr);
		entity.setUseDesc(useDesc);
		entity.setAddUser(userEntity.getUsername());
		entity.setAddTime(new Date());
		return entity;
	}
}
