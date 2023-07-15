package com.yuqian.itax.snapshot.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 订单快照(已完成)
 * 
 * @Date: 2020年10月26日 11:25:17 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_order_snapshot")
public class OrderSnapshotEntity implements Serializable {
	
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
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 用户类型 1-会员 2-城市合伙人 3-高级城市合伙人 4-平台 5-管理员 6-其他
	 */
	private Integer userType;
	
	/**
	 * 订单类型 1-充值 2-代理充值 3-提现 4-代理提现 5 - 工商注册 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现 12-消费开票
	 */
	private Integer orderType;
	
	/**
	 * 产品id (会员升级对应等级id)
	 */
	private Long productId;
	
	/**
	 * 会员升级等级   0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5城市服务商
	 */
	private Integer upgradeLevelNo;
	
	/**
	 * 支付金额(分)
	 */
	private Long payAmount;
	
	/**
	 * 服务费(分)
	 */
	private Long serviceFee;
	
	/**
	 * 可分润金额(分)
	 */
	private Long profitAmount;
	
	/**
	 * 机构利润(分)
	 */
	private Long oemProfits;
	
	/**
	 * 快照时间(订单完成时间)
	 */
	private Date snapshotTime;
	
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
