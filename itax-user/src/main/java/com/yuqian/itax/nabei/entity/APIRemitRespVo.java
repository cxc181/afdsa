package com.yuqian.itax.nabei.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 对账文件下载响应参数实体
 */
@Getter
@Setter
public class APIRemitRespVo extends NabeiAPIBaseResp implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 对账文件BASE64编码字符串
	 */
	private String p3_data;
	
	/**
	 * 签名
	 */
	private String p4_hmac;

}
