package com.yuqian.itax.park.entity.vo;

import com.google.common.collect.Lists;
import com.yuqian.itax.common.util.CollectionUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 增值税税率
 * @Date: 2020年07月14日 15:40:35
 * @author yejian
 */
@Getter
@Setter
public class TaxRulesVatRateVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 税率
	 */
	private BigDecimal vatRate;

	/**
	 * 是否允许开普票 0-不允许 1-允许
	 */
	private Integer isOpenPp;

	/**
	 * 是否允许开专票 0-不允许 1-允许
	 */
	private Integer isOpenZp;

	public static List<TaxRulesVatRateVO> getTaxRulesVatRateVO(List<BigDecimal> list) {
		List<TaxRulesVatRateVO> vo = Lists.newArrayList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (BigDecimal vatRate : list) {
				TaxRulesVatRateVO vatRateVO = new TaxRulesVatRateVO();
				vatRateVO.setVatRate(vatRate);
				vo.add(vatRateVO);
			}
		}
		return vo;
	}
}
