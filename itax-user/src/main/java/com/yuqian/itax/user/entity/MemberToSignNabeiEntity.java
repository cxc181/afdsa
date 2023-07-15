package com.yuqian.itax.user.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 会员签约纳呗表
 * 
 * @Date: 2020年06月23日 09:51:00 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_to_sign_nabei")
public class MemberToSignNabeiEntity implements Serializable {
	
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
	private Long memberId;
	
	/**
	 * 等级标识 -1-员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer levelNo;
	
	/**
	 * 开户银行
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
	 * 签约状态 0-未签约，1-已签约，2-已解约
	 */
	private Integer signStatus;
	
	/**
	 * 签约失败原因
	 */
	private String signErrorMsg;
	
	/**
	 * 签约协议号
	 */
	private String protocolno;
	
	/**
	 * 签约账号类型 1-银行卡
	 */
	private Integer signAccounttyp;
	
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
