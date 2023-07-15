package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 企业注销订单表
 * 
 * @Date: 2020年02月13日 15:33:29 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_company_cancel_order")
public class CompanyCancelOrderEntity implements Serializable {
	
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
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 企业id
	 */
	private Long companyId;
	
	/**
	 * 累计开票额度(分)
	 */
	private Long cancelTotalLimit;
	
	/**
	 * 注销服务费（分）
	 */
	private Long cancelServiceCharge;
	
	/**
	 * 企业类型1-个体开户 2-个独开户 3-有限合伙 4-有限责任
	 */
	private Integer companyType;
	
	/**
	 * 企业名称
	 */
	private String companyName;
	
	/**
	 * 附件地址
	 */
	private String attachmentAddr;
	
	/**
	 * 经办人账号
	 */
	private String agentAccount;
	
	/**
	 * 操作人类型  0-用户本人 1-系统  2-经办人
	 */
	private Integer operUserType;
	
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
