package com.yuqian.itax.tax.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 待处理税单VO
 * 
 * @Date: 2022/3/11
 * @author lmh
 */
@Getter
@Setter
public class PendingTaxBillVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	private Long companyTaxBillId;

	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;

	/**
	 * 税款所属期-季度
	 */
	private Integer taxBillSeasonal;

	/**
	 * 税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税 6-待核对   7-待填报成本 8-待申报 9-已作废 10-待财务审核 11-审核不通过
	 */
	private Integer taxBillStatus;

	/**
	 * 所得税征收方式 1-查账征收 2-核定征收
	 */
	private int incomeLevyType;

	/**
	 * 时间差（天） （截止日期 - 当前时间），负数代表超时
	 */
	private int timeDifference;

	/**
	 * 用户id
	 */
	private Long memberId;

	/**
	 * 用户手机号
	 */
	private String memberPhone;

	/**
	 * 经营者手机号
	 */
	private String operatorTel;

	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 季度开票金额
	 */
	private Long invoiceMoney;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 是否发送超时确认成本提醒通知 0-未发送 1-已发送
	 */
	private int isSendNotice;

	public PendingTaxBillVO() {

	}

}
