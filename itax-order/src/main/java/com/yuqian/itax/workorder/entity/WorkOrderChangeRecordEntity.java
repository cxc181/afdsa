package com.yuqian.itax.workorder.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 工单变更记录
 * 
 * @Date: 2019年12月07日 20:00:33 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_work_order_change_record")
public class WorkOrderChangeRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 工单号
	 */
	private String workOrderNo;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 工单类型 1- 办理核名 2-开票审核 3-流水审核
	 */
	private Integer workOrderType;
	
	/**
	 * 工单状态 类型为开户： 0-待接单 1-处理中 2-已完成 3-已取消 
                              类型为开票： 0-待接单 1-审核中 2-审核通过 3-审核未通过
	 */
	private Integer workOrderStatus;
	
	/**
	 * 工单备注
	 */
	private String workOrderDesc;
	
	/**
	 * 处理人名称
	 */
	private String processorName;
	
	/**
	 * 处理人账号
	 */
	private String processorAccount;
	
	/**
	 * 处理人类型 0-本人 1-客服 2-经办人 3-管理员
	 */
	private Integer processorType;
	/**
	 * 坐席id
	 */
	private Long customerServiceId;
	/**
	 * 坐席名称
	 */
	private String customerServiceName;

	/**
	 * 坐席客户账号
	 */
	private String customerServiceAccount;
	
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
