package com.yuqian.itax.product.entity.vo;

import com.yuqian.itax.product.entity.ChargeStandardEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 收费标准
 * 
 * @Date: 2019年12月07日 20:42:23 
 * @author 蒋匿
 */
@Getter
@Setter
public class ChargeStandardVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 费用金额最小
	 */
	private BigDecimal chargeMin;
	
	/**
	 * 费用金额最大
	 */
	private BigDecimal chargeMax;
	
	/**
	 * 收费比率
	 */
	private BigDecimal chargeRate;

	public ChargeStandardVO() {
	}
	public ChargeStandardVO(ChargeStandardEntity charge) {
		if (charge.getChargeMax() != null) {
			this.chargeMax = new BigDecimal(charge.getChargeMax()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP);
		}else {
			this.chargeMax = null;
		}
		if (charge.getChargeMin() != null) {
			this.chargeMin = new BigDecimal(charge.getChargeMin()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_UP);
		}else {
			this.chargeMin = null;
		}
		this.chargeRate = charge.getChargeRate();
	}
}
