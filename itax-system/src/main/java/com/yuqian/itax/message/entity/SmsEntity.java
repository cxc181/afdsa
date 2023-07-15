package com.yuqian.itax.message.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 短信记录表
 * 
 * @Date: 2019年12月08日 20:44:18 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="sys_e_sms")
public class SmsEntity implements Serializable {
	
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
	 * 用户类型 1-前端用户 2-后台用户
	 */
	private Integer userType;
	
	/**
	 * 手机号码
	 */
	private String userPhone;
	
	/**
	 * 短信内容
	 */
	private String smsContent;
	
	/**
	 * 短信模板id
	 */
	private Long smsTmplId;
	
	/**
	 * 预约发送时间
	 */
	private Date reserveSendTime;
	
	/**
	 * 发送时间
	 */
	private Date sendTime;
	
	/**
	 * 发送状态 0-待发送 1-已发送 2-发送失败 3-重新发送
	 */
	private Integer sendStatus;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 返回码
	 */
	private String retCode;
	
	/**
	 * 返回信息
	 */
	private String retMsg;
	
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
