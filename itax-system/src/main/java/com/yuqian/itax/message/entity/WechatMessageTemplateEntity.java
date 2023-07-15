package com.yuqian.itax.message.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 微信订阅消息模板配置
 * 
 * @Date: 2020年06月04日 16:39:53 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_wechat_message_template")
public class WechatMessageTemplateEntity implements Serializable {
	
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
	 * 模板类型 1-工商开户审核
	 */
	private Integer templateType;
	
	/**
	 * 微信模板id
	 */
	private String wechatTemplateId;
	
	/**
	 * 模板参数 json
	 */
	private String templateParams;
	
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
