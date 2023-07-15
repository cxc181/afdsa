package com.yuqian.itax.corporateaccount.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 对公户申请订单
 * 
 * @Date: 2020年09月07日 09:12:13 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_corporate_account_apply_order_change")
public class CorporateAccountApplyOrderChangeEntity implements Serializable {
	
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
	private Long memberId;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 申请银行
	 */
	private String applyBankName;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 办理费用(分)
	 */
	private Long handleFee;
	
	/**
	 * 账户托管费(分)
	 */
	private Long escrowFee;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 园区编码
	 */
	private String parkCode;
	
	/**
	 * 对公户id
	 */
	private Long corporateAccountId;
	
	/**
	 * 状态 0-待支付 1-等待预约  2-已完成 3-已取消
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

	/**
	 * 付款完成时间
	 */
	private Date payTime;

	/**
	 * 银行总部名称
	 */
	private String headquartersName;

	/**
	 * 银行总部编号
	 */
	private String headquartersNo;
}
