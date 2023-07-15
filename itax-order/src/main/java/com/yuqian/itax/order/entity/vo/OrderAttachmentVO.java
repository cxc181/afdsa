package com.yuqian.itax.order.entity.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单附件表
 * 
 * @Date: 2019年12月14日 11:43:05 
 * @author 蒋匿
 */
@Getter
@Setter
public class OrderAttachmentVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 附件地址
	 */
	private String attachmentAddr;
	
	/**
	 * 添加时间
	 */
	private Date addTime;
	
}
