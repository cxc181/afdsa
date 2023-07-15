package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.common.util.CollectionUtil;
import com.yuqian.itax.user.entity.CompanyInvoiceCategoryEntity;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 企业开票类目表
 * 
 * @Date: 2020年05月18日 10:18:18 
 * @author 蒋匿
 */
@Getter
@Setter
public class CompanyInvoiceCategoryVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 *基础类目id
	 */
	private Long categoryBaseId;

	/**
	 * 类目名称
	 */
	private String categoryName;

	public CompanyInvoiceCategoryVO(CompanyInvoiceCategoryEntity entity) {
		if (entity == null) {
			return;
		}
		this.categoryBaseId = entity.getCategoryBaseId();
		this.categoryName = entity.getCategoryName();
	}

	public static List<CompanyInvoiceCategoryVO> toListVO(List<CompanyInvoiceCategoryEntity> list) {
		List<CompanyInvoiceCategoryVO> result = Lists.newArrayList();
		if (CollectionUtil.isEmpty(list)) {
			return result;
		}
		for (CompanyInvoiceCategoryEntity entity : list) {
			result.add(new CompanyInvoiceCategoryVO(entity));
		}
		return result;
	}

}
