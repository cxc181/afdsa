package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 企业证件申请、归还返回VO
 * @Date: 2020年07月21日 10:30:46
 * @author yejian
 */
@Getter
@Setter
public class CompanyCertListApiVO implements Serializable {

	private static final long serialVersionUID = -1L;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 企业名称
	 */
	private Long companyName;

	/**
	 * 申请类型  1-领用  2-归还
	 */
	private Integer applyType;

	/**
	 * 资源类型 1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章 ，多个资源直接用逗号分割
	 */
	private String applyResouces;

	/**
	 * 快递单号
	 */
	private String courierNumber;

	/**
	 * 快递公司名称
	 */
	private String courierCompanyName;

	/**
	 * 签收资源 1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章  多个资源直接用逗号分割
	 */
	private String signResouces;

	/**
	 * 签收时间
	 */
	private Date signTime;

	/**
	 * 状态  1-待发货  2-出库中 3-待签收 4-已签收 5-已取消
	 */
	private Integer status;

}
