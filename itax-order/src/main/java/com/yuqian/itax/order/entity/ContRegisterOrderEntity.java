package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 续费订单表
 * 
 * @Date: 2021年02月03日 11:27:11 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_cont_register_order")
public class ContRegisterOrderEntity implements Serializable {
	
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
	 * 会员id
	 */
	private Long memeberId;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 对公户id
	 */
	private Long corporateAccountId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 订单金额(分)
	 */
	private Long orderAmount;
	
	/**
	 * 支付金额(分)
	 */
	private Long payAmount;
	
	/**
	 * 续费类型 1-托管费 2-对公户
	 */
	private Integer contType;
	
	/**
	 * 订单状态 0-待支付 1-支付中 2-已完成 3-已取消
	 */
	private Integer orderStatus;
	
	/**
	 * 完成时间
	 */
	private Date completeTime;
	
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
