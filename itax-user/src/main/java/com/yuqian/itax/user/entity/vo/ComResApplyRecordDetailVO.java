package com.yuqian.itax.user.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业资源申请记录详情前端展示bean
 * @Date: 2020年03月25日 09:30:46
 * @author yejian
 */
@Getter
@Setter
public class ComResApplyRecordDetailVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 企业id
	 */
	private Long companyId;

	/**
	 * 企业名称
	 */
	private String companyName;
	
	/**
	 * 申请类型  1-领用  2-归还
	 */
	private Integer applyType;
	
	/**
	 * 资源类型 1-公章 2-财务章 3-对公账号u盾 4-营业执照  5-营业执照副本 6-发票章 ，多个资源直接用 逗号分割
	 */
	private String applyResouces;
	
	/**
	 * 状态 0-待付款 1-待发货 2-出库中 3-待签收 4-已签收 5-已取消
	 */
	private Integer status;
	
	/**
	 * 快递费
	 */
	private Long postageFees;
	
	/**
	 * 收件人姓名
	 */
	private String recipient;
	
	/**
	 * 收件人手机号
	 */
	private String recipientPhone;

	/**
	 * 收件人省名称
	 */
	private String provinceName;

	/**
	 * 收件人市名称
	 */
	private String cityName;
	
	/**
	 * 收件人区名称
	 */
	private String districtName;
	
	/**
	 * 收件人详细地址
	 */
	private String recipientAddress;
	
	/**
	 * 快递单号
	 */
	private String courierNumber;
	
	/**
	 * 快递公司名称
	 */
	private String courierCompanyName;
	
	/**
	 * 添加时间
	 */
	private Date addTime;

}
