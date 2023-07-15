package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * 第三方开票-推送回调数据DTO
 */
@Getter
@Setter
public class ThirdPartyPushVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 快开机构编码
	 */
	private String oemCode;

	/**
	 * 第三方秘钥
	 */
	private String secretKey;

	/**
	 * 回调地址
	 */
	private String callbackUrl;

	/**
	 * 请求参数
	 */
	private Map<String, Object> param;

	/**
	 * 重试次数
	 */
	private int num = 3;
}
