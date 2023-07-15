package com.yuqian.itax.tax.entity.vo;

import com.yuqian.itax.tax.entity.CompanyTaxBillEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 企业税单表
 * 
 * @Date: 2020年12月03日 10:36:23 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyTaxBillVouchersVO implements Serializable {

	private static final long serialVersionUID = -1L;


	/**
	 * 名称
	 */
	private String name;

	/**
	 * 图片地址
	 */
	private String picUrl;

	public CompanyTaxBillVouchersVO() {
	}
}
