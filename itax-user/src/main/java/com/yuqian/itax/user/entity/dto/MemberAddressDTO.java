package com.yuqian.itax.user.entity.dto;

import com.yuqian.itax.user.entity.MemberAddressEntity;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 会员收件地址
 * @author：pengwei
 * @Date：2020/12/25 16:57
 * @version：1.0
 */
@Getter
@Setter
public class MemberAddressDTO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	@NotNull(message = "是否默认状态不能为空", groups = {Update.class})
	private Long id;

	/**
	 * 收件人
	 */
	@NotBlank(message = "收件人不能为空", groups = {Add.class, Update.class})
	@Size(max = 32, message = "收件人不能超过32位字符", groups = {Add.class, Update.class})
	private String recipient;

	/**
	 * 联系电话
	 */
	@NotBlank(message = "联系电话不能为空", groups = {Add.class, Update.class})
	@Size(max = 16, message = "联系电话不能超过16位字符", groups = {Add.class, Update.class})
//	@Phone(message = "收件人手机号码格式错误", groups = {Add.class, Update.class})
	private String recipientPhone;

	/**
	 * 详细地址
	 */
	@NotBlank(message = "详细地址不能为空", groups = {Add.class, Update.class})
	@Size(max = 128, message = "详细地址不能超过128位字符", groups = {Add.class, Update.class})
	private String recipientAddress;

	/**
	 * 省编码
	 */
	@NotBlank(message = "省编码不能为空", groups = {Add.class, Update.class})
	@Size(max = 16, message = "省编码不能超过16位字符", groups = {Add.class, Update.class})
	private String provinceCode;

	/**
	 * 省名称
	 */
	@NotBlank(message = "省名称不能为空", groups = {Add.class, Update.class})
	@Size(max = 32, message = "省名称不能超过32位字符", groups = {Add.class, Update.class})
	private String provinceName;

	/**
	 * 市编码
	 */
	@NotBlank(message = "市编码不能为空", groups = {Add.class, Update.class})
	@Size(max = 16, message = "市编码不能超过16位字符", groups = {Add.class, Update.class})
	private String cityCode;

	/**
	 * 市名称
	 */
	@NotBlank(message = "市名称不能为空", groups = {Add.class, Update.class})
	@Size(max = 32, message = "市名称不能超过32位字符", groups = {Add.class, Update.class})
	private String cityName;

	/**
	 * 区编码
	 */
	@NotBlank(message = "区编码不能为空", groups = {Add.class, Update.class})
	@Size(max = 16, message = "区编码不能超过16位字符", groups = {Add.class, Update.class})
	private String districtCode;

	/**
	 * 区名称
	 */
	@NotBlank(message = "区名称不能为空", groups = {Add.class, Update.class})
	@Size(max = 32, message = "区名称不能超过32位字符", groups = {Add.class, Update.class})
	private String districtName;

	/**
	 * 是否默认 0-不默认 1-默认
	 */
	@NotNull(message = "是否默认状态不能为空", groups = {Add.class, Update.class})
	@Min(value = 0, message = "是否默认状态设置有误", groups = {Add.class, Update.class})
	@Max(value = 1, message = "是否默认状态设置有误", groups = {Add.class, Update.class})
	private Integer isDefault;

	public MemberAddressEntity toEntity(Long memberId, String oemCode) {
		MemberAddressEntity entity = new MemberAddressEntity();
		entity.setMemberId(memberId);
		entity.setOemCode(oemCode);
		entity.setRecipient(recipient);
		entity.setRecipientPhone(recipientPhone);
		entity.setRecipientAddress(recipientAddress);
		entity.setProvinceCode(provinceCode);
		entity.setProvinceName(provinceName);
		entity.setCityCode(cityCode);
		entity.setCityName(cityName);
		entity.setDistrictCode(districtCode);
		entity.setDistrictName(districtName);
		entity.setIsDefault(isDefault);
		return entity;
	}
}

