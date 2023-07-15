package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.user.entity.CompanyResourcesAddressEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 企业资源所在地管理
 * 
 * @Date: 2019年12月14日 13:56:31 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyResourcesAddressVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private Long id;
	/**
	 * 企业名称
	 */
	private String CompanyName;
	/**
	 * 企业id
	 */
	private Long CompanyId;
	/**
	 * 资源类型  1-公章 2-财务章 3-对公账号u盾 4-营业执照 
	 */
	private Integer resourcesType;
	
	/**
	 * 所在地
	 */
	private String address;

	/**
	 * 所在地变更为
	 */
	private List<Map<String,Object>> addressSelect;
	/**
	 * 备注
	 */
	private String remark;

	public CompanyResourcesAddressVO() {

	}
	public CompanyResourcesAddressVO(CompanyResourcesAddressEntity entity) {
		this.resourcesType = entity.getResourcesType();
		this.address = entity.getAddress();
	}
}
