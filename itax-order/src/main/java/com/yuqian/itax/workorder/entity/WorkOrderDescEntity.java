package com.yuqian.itax.workorder.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 工单备注表
 * 
 * @Date: 2021年08月04日 17:17:26 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_work_order_desc")
public class WorkOrderDescEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 工单号
	 */
	private String workOrderNo;
	
	/**
	 * 备注内容
	 */
	private String descContent;
	
	/**
	 * 操作坐席账号
	 */
	private String customerServiceAccount;
	
	/**
	 * 操作坐席名称
	 */
	private String customerServiceName;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
	/**
	 * 添加人
	 */
	private String addUser;
	
	/**
	 * 修改时间
	 */
	private Date updateTime;
	
	/**
	 * 修改人
	 */
	private String updateUser;
	
	/**
	 * 备注
	 */
	private String remark;
	
	
}
