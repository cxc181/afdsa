package com.yuqian.itax.snapshot.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * oem机构资金流统计
 * 
 * @Date: 2020年10月26日 10:48:25 
 * @author 蒋匿
 */
@Getter
@Setter
public class OemCapitalQuery extends BaseQuery {

	/**
	 * 机构编码
	 */
	private String oemCode;
	
}
