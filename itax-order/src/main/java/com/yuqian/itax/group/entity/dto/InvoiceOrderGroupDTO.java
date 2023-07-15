package com.yuqian.itax.group.entity.dto;

import com.yuqian.itax.group.entity.InvoiceHeadGroupEntity;
import com.yuqian.itax.group.entity.InvoiceOrderGroupEntity;
import com.yuqian.itax.group.enums.InvoiceOrderGroupStatusEnum;
import com.yuqian.itax.system.entity.InvoiceCategoryBaseEntity;
import com.yuqian.itax.user.entity.UserEntity;
import com.yuqian.itax.util.util.MoneyUtil;
import com.yuqian.itax.util.validator.Add;
import com.yuqian.itax.util.validator.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 集团开票订单
 * 
 * @Date: 2020年03月04日 09:25:55 
 * @author 蒋匿
 */
@Getter
@Setter
public class InvoiceOrderGroupDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 集团发票抬头Id
	 */
	@NotNull(message="集团发票抬头Id不能为空", groups={Add.class})
	private Long headGroupId;

	/**
	 * 开票类目id
	 */
	@NotNull(message="开票类目Id不能为空", groups={Add.class})
	private Long categoryBaseId;
	
	/**
	 * 发票类型 1-普通发票 2-增值税发票
	 */
	@NotNull(message="发票类型不能为空", groups={Add.class})
	@Min(value = 1, message = "发票类型有误", groups={Add.class})
	@Max(value = 2, message = "发票类型有误", groups={Add.class})
	private Integer invoiceType;


	/**
	 * 发票偏好 1-全部纸质发票 2-优先电子发票
	 */
	@NotNull(message="发票偏好不能为空", groups={Add.class})
	@Min(value = 1, message = "发票偏好有误", groups={Add.class})
	@Max(value = 2, message = "发票偏好有误", groups={Add.class})
	private Integer invoiceWay;
	/**
	 * 银行流水截图
	 */
	@NotBlank(message="打款流水不能为空", groups={Add.class, Update.class})
	@Size(max = 200, message = "打款流水不能超过200个字", groups={Add.class, Update.class})
	private String accountStatement;

	/**
	 * 增值税税率
	 */
	@NotNull(message="增值税税率不能为空", groups={Add.class})
	@Min(value = 0, message = "增值税税率必须大于0", groups={Add.class})
	private BigDecimal vatFeeRate;

	public InvoiceOrderGroupEntity toEntity(InvoiceCategoryBaseEntity categoryEntity, InvoiceHeadGroupEntity headGroupEntity, UserEntity userEntity) {
		InvoiceOrderGroupEntity entity = new InvoiceOrderGroupEntity();
		entity.setOemCode(this.oemCode);
		entity.setInvoiceType(this.invoiceType);
		entity.setAccountStatement(this.accountStatement);
		entity.setCategoryGroupId(categoryEntity.getId());
		entity.setCategoryGroupName(categoryEntity.getTaxClassificationAbbreviation()+"*"+categoryEntity.getGoodsName());
		entity.setBankName(headGroupEntity.getBankName());
		entity.setBankNumber(headGroupEntity.getBankNumber());
		entity.setCityCode(headGroupEntity.getCityCode());
		entity.setCityName(headGroupEntity.getCityName());
		entity.setCompanyAddress(headGroupEntity.getCompanyAddress());
		entity.setCompanyName(headGroupEntity.getCompanyName());
		entity.setDistrictCode(headGroupEntity.getDistrictCode());
		entity.setDistrictName(headGroupEntity.getDistrictName());
		entity.setEin(headGroupEntity.getEin());
		//发票类型 1-普通发票 2-增值税发票
		entity.setInvoiceTypeName(Objects.equals(this.invoiceType, 1) ? "增值税普通发票" : "增值税专用发票");
		entity.setPhone(headGroupEntity.getPhone());
		entity.setProvinceCode(headGroupEntity.getProvinceCode());
		entity.setProvinceName(headGroupEntity.getProvinceName());
		entity.setOrderStatus(InvoiceOrderGroupStatusEnum.CREATED.getValue());
		entity.setInvoiceWay(1);
		entity.setRecipient(headGroupEntity.getRecipient());
		entity.setRecipientAddress(headGroupEntity.getRecipientAddress());
		entity.setRecipientPhone(headGroupEntity.getRecipientPhone());
		entity.setRegistAddress(headGroupEntity.getRegistAddress());
		entity.setAddUser(userEntity.getUsername());
		entity.setAddTime(new Date());
		entity.setVatFeeRate(MoneyUtil.fen2yuan(this.vatFeeRate));
		return entity;
	}
}
