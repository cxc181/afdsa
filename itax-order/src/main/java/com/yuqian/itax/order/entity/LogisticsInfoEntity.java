package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 开票快递信息记录
 * 
 * @Date: 2020年02月13日 15:33:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_logistics_info")
public class LogisticsInfoEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 快递单号
	 */
	private String courierNumber;
	
	/**
	 * 快递公司名称
	 */
	private String courierCompanyName;
	
	/**
	 * 物流信息
	 */
	private String logisticsInfo;
	
	/**
	 * 快递时间
	 */
	private Date logisticsTime;
	
	/**
	 * 快递状态 0-待发货 1-已揽货 2-运输中 3-派送中 4-待取件  5-已签收 6-已收货
	 */
	private Integer logisticsStatus;
	
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
