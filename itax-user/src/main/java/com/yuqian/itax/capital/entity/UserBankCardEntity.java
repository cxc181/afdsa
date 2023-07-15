package com.yuqian.itax.capital.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 银行卡管理
 * 
 * @Date: 2019年12月07日 20:54:44 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_user_bank_card")
public class UserBankCardEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 开户行
	 */
	private String bankName;
	/**
	 * 银行编码
	 */
	private String bankCode;

	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 身份证
	 */
	private String idCard;
	
	/**
	 * 预留手机号
	 */
	private String phone;
	
	/**
	 * 银行卡号
	 */
	private String bankNumber;
	
	/**
	 * 银行卡状态  0-解绑 1-绑定 2-禁用
	 */
	private Integer status;
	
	/**
	 * 银行卡类型 1-储蓄卡 2-信用卡
	 */
	private Integer bankCardType;

	/**
	 * 易税银行卡ID
	 */
	private Long professionalBankId;
	
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
	 * 用户类型 1-会员 2-系统用户
	 */
	private Integer userType;

}
