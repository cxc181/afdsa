package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.user.entity.MemberAddressEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 会员收件地址表
 * 
 * @Date: 2020年12月25日 14:35:49 
 * @author 蒋匿
 */
@Getter
@Setter
public class MemberAddressVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 收件人
	 */
	private String recipient;
	
	/**
	 * 联系电话
	 */
	private String recipientPhone;
	
	/**
	 * 详细地址
	 */
	private String recipientAddress;


	/**
	 * 省编码
	 */
	private String provinceCode;

	/**
	 * 省名称
	 */
	private String provinceName;

	/**
	 * 市编码
	 */
	private String cityCode;

	/**
	 * 市名称
	 */
	private String cityName;

	/**
	 * 区编码
	 */
	private String districtCode;

	/**
	 * 区名称
	 */
	private String districtName;

	/**
	 * 是否默认 0-不默认 1-默认
	 */
	private Integer isDefault;

	public MemberAddressVO() {

	}

	public MemberAddressVO(MemberAddressEntity entity) {
		if (entity == null) {
			return;
		}
		this.id = entity.getId();
		this.recipient = entity.getRecipient();
		this.recipientPhone = entity.getRecipientPhone();
		this.recipientAddress = entity.getRecipientAddress();
		this.provinceCode = entity.getProvinceCode();
		this.provinceName = entity.getProvinceName();
		this.cityCode = entity.getCityCode();
		this.cityName = entity.getCityName();
		this.districtCode = entity.getDistrictCode();
		this.districtName = entity.getDistrictName();
		this.isDefault = entity.getIsDefault();
	}
}
