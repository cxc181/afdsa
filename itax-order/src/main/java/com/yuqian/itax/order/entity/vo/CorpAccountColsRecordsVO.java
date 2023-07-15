package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 企业对公户收款记录VO
 * @Author  Kaven
 * @Date   2020/9/9 16:31
*/
@Getter
@Setter
public class CorpAccountColsRecordsVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	/**
	 * 银行唯一编号
	 */
	private String bankCollectionRecordNo;

	/**
	 * 收款时间
	 */
	private Date tradingTime;

	/**
	 * 使用额度
	 */
	private Long useAmount;

	/**
	 * 总额度
	 */
	private Long totalAmount;
}
