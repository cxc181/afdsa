package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 用户订单日统计
 * 
 * @Date: 2020年06月03日 09:13:20 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_user_order_statistics_day")
public class UserOrderStatisticsDayEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 会员id
	 */
	private Long userId;
	
	/**
	 * 会员等级 -1 - 员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer userLevelNo;
	
	/**
	 * 上级用户id
	 */
	private Long parentUserId;
	
	/**
	 * 直推用户
	 */
	private Integer promoteUserDirect;
	
	/**
	 * 直推个体数
	 */
	private Integer individualDirect;
	
	/**
	 * 直推企业注册费
	 */
	private Long companyRegistFeeDirect;
	
	/**
	 * 直推开票服务费
	 */
	private Long invoiceFeeDirect;
	
	/**
	 * 直推注销服务费
	 */
	private Long companyCancelFeeDirect;
	
	/**
	 * 直推会员升级费
	 */
	private Long memberUpgradeFeeDirect;
	
	/**
	 * 直推分润费
	 */
	private Long profitsFeeDirect;
	
	/**
	 * 直推开票金额
	 */
	private Long invoiceAmountDirect;
	
	/**
	 * 裂变用户数
	 */
	private Integer promoteUserFission;
	
	/**
	 * 裂变个体数
	 */
	private Integer individualFission;
	
	/**
	 * 裂变企业注册费
	 */
	private Long companyRegistFeeFission;
	
	/**
	 * 裂变开票服务费
	 */
	private Long invoiceFeeFission;
	/**
	 * 裂变会员升级费
	 */
	private Long memberUpgradeFeeFission;

	/**
	 * 裂变注销服务费
	 */
	private Long companyCancelFeeFission;
	
	/**
	 * 裂变分润费
	 */
	private Long profitsFeeFission;
	
	/**
	 * 裂变开票金额
	 */
	private Long invoiceAmountFission;

	/**
	 * 直推托管费续费
	 */
	private Long custodyFeeRenewalDirect;

	/**
	 * 裂变托管费续费
	 */
	private Long custodyFeeRenewalFission;

	/**
	 *统计日期
	 */
	private Date statisticsDay;
	
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
