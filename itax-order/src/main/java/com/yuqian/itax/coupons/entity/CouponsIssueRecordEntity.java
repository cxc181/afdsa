package com.yuqian.itax.coupons.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 优惠卷发放记录表
 * 
 * @Date: 2021年04月08日 10:43:43 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_coupons_issue_record")
public class CouponsIssueRecordEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 优惠卷id
	 */
	private Long couponsId;

	/**
	 * 兑换码id
	 */
	private Long couponsExchangeId;
	
	/**
	 * 优惠卷编码
	 */
	private String couponsCode;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;

	/**
	 * 发放方式 0-批量发放 1-兑换码
	 */
	private Integer issueType;
	
	/**
	 * 发放时间
	 */
	private Date issueTime;
	
	/**
	 * 使用时间
	 */
	private Date useTime;
	
	/**
	 * 操作人
	 */
	private String operUser;
	
	/**
	 * 状态  0-未使用 1-已使用 2-已过期 3-已撤回
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
