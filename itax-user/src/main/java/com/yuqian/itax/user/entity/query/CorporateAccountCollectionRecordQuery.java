package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description 企业对公账户明细参数接收BEAN
 * @Author  Kaven
 * @Date   2020/9/7 16:11
*/
@Getter
@Setter
public class CorporateAccountCollectionRecordQuery extends BaseQuery implements Serializable {

	private static final long serialVersionUID = -1L;

	private Long userId;// 用户ID

	@NotNull(message = "对公户ID不能为空")
	private Long corporateAccountId;// 对公户ID

	private Long recordId; //收款记录id

	private Long companyId;// 企业ID

	private String companyName;// 企业名称

	private String month; // 按月份查询

	private String day;// 按日查询

	private String otherPartyBankNumber;// 对方账户

	private String otherPartyBankAcount;// 对方账户名

	private String bankCollectionRecordNo;// 银行唯一编号

	private String txnStffId;// 制单员编号

	private String draweeAccountNo;// 委托单位账号

	private String smy;// 交易备注

	private String tradingRemark;//摘要

	private String remark;// 备注

	private String isChoose;// 是否为"选择收款记录"入口标识 1-是 0-否

	private Integer tradingStatus;// 交易状态 1-支出 2-收入

	private Integer isSystem; // 用于区分是否手动调用收款记录 默认自动 1-手动

}