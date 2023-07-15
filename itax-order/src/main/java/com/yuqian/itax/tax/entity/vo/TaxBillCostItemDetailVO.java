package com.yuqian.itax.tax.entity.vo;

import com.yuqian.itax.tax.entity.CompanyTaxCostItemEntity;
import com.yuqian.itax.tax.entity.dto.CostItemDTO;
import com.yuqian.itax.tax.entity.dto.FillCostDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 企业税单成本明细VO
 * 
 * @Date: 2022/3/11
 * @author lmh
 */
@Getter
@Setter
public class TaxBillCostItemDetailVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 成本项列表
	 */
	private List<CompanyTaxCostItemEntity> costItemEntityList;

	/**
	 * 成本项图片
	 */
	private List<String> costItemImgList;

	public TaxBillCostItemDetailVO() {

	}
}
