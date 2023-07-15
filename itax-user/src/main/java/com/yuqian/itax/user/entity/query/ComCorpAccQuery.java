package com.yuqian.itax.user.entity.query;

import com.yuqian.itax.common.base.entity.query.BaseQuery;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 企业对公户查询参数接收BEAN
 * @Author  Kaven
 * @Date   2020/9/7 11:09
*/
@Getter
@Setter
public class ComCorpAccQuery extends BaseQuery implements Serializable {

	private static final long serialVersionUID = -1L;

	private Long currUserId;// 当前用户ID

	private Long corporateAccountId;// 对公户ID

	private String month; // 按月份查询

	private String day;// 按日查询

	private String txnStffId;// 制单员编号

	private String draweeAccountNo;// 委托单位账号
}