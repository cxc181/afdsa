package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *  @Author: Kaven
 *  @Date: 2020/3/4 10:06
 *  @Description: banner详情返回实体bean
 */
@Getter
@Setter
public class BannerVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 分享标题
	 */
	private String shareTitle;

	/**
	 * 分享图地址
	 */
	private String shareImageAddress;
}
