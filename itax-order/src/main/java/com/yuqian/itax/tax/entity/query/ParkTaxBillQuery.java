package com.yuqian.itax.tax.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 园区税单查询
 *
 * @Date: 2020年12月03日 10:36:14 
 * @author 蒋匿
 */
@Getter
@Setter
public class ParkTaxBillQuery extends BaseQuery implements Serializable {
	private static final long serialVersionUID = -1L;

	/**
	 * 园区税单主键id
	 */
	private Long id;
	/**
	 * 税款所属期年
	 */
	private Integer taxBillYear;

	/**
	 * 税款所属期
	 */
	private Integer taxBillSeasonal;

	/**
	 * 会员id
	 */
	private Long memberId;

	/**
	 * 企业ID
	 */
	private Long companyId;

	/**
	 * 税单类型 1-全部，2-待处理
	 */
	private Integer taxBillType;

	/**
	 * 园区ID
	 */
	private Long parkId;
	/**
	 * 上传文件地址
	 */
	private String fileUrl;

	/**
	 * 超时时间
	 */
	private Integer overTime;
	/**
	 * 税单状态 0-待确认 1-待退税 2-待补税 3-正常 4-已退税 5-已补税
	 */
	private Integer taxBillStatus;

	/**
	 *凭证状态 0-未上传 1-解析中 2-已上传3-部分已上传
	 */
	private Integer vouchersStatus;

}
