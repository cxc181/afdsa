package com.yuqian.itax.message.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 短信模板配置
 * 
 * @Date: 2019年12月08日 20:44:39 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_sms_template")
public class SmsTemplateEntity implements Serializable {
	
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
	 * 模板类型 1-验证码 2-开户 3-开票 4-资金变动 5-异常订单通知 6-绑卡 7-解绑 8-提现 9-余额支付
	 */
	private Integer templateType;
	
	/**
	 * 模板内容
	 */
	private String templateContent;
	
	/**
	 * 状态 0-不可用 1-可用
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
