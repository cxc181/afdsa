package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.*;
/**
 * 会员消费记录表
 * 
 * @Date: 2020年09月27日 11:22:19 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_consumption_record")
public class MemberConsumptionRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 订单类型  3-提现 4-代理提现 5 - 工商注册 6-开票 7-用户升级 8-工商注销 9-证件申请 10-对公户申请 11-对公户提现
	 */
	@Excel( name="订单类型" , replace = { "提现_3","代理提现_4","企业注册_5","开票_6","用户升级_7","工商注销_8","证件申请_9","对公户申请_10","对公户提现_11","托管费续费_15"  } )
	private Integer orderType;
	/**
	 * 订单号
	 */
	@Excel( name = "订单编号")
	private String orderNo;
	

	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 消费金额(分)(对应订单的服务费)
	 */
	@Excel( name = "开票金额")
	private Long consumptionAmount;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 是否已开票 0-未开 1-已开
	 */
	private Integer isOpenInvoice;
	
	/**
	 * 添加时间
	 */
	@Excel(name = "订单完成时间" , replace = {"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
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
