package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 接单表
 * 
 * @Date: 2019年12月16日 20:20:33 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_receive_order")
public class ReceiveOrderEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 客服id，系统用户id
	 */
	private Long userId;
	
	/**
	 * 接单数量
	 */
	private Integer receiveOrderNum;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 状态 0-不可用 1-可用
	 */
	private Integer status;
	
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
