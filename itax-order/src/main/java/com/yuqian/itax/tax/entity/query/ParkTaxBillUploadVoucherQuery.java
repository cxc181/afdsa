package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 园区税单上传完税凭证
 *
 * @Date: 2020年12月03日 10:36:14 
 * @author 蒋匿
 */
@Getter
@Setter
public class ParkTaxBillUploadVoucherQuery extends BaseQuery implements Serializable {
	private static final long serialVersionUID = -1L;

	/**
	 * 园区税单主键id
	 */
	private Long id;
	/**
	 * 上传文件地址
	 */
	private String fileUrl;
}
