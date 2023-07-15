package com.yuqian.itax.message.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 微信订阅消息模板配置
 * 
 * @Date: 2020年06月04日 16:39:53 
 * @author 蒋匿
 */
@Getter
@Setter
public class WechatMessageTemplateSimpleVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 模板类型 1-工商开户审核 2-邀请签名 3-签名确认结果
	 */
	private Integer templateType;
	
	/**
	 * 微信模板id
	 */
	private String wechatTemplateId;

}
