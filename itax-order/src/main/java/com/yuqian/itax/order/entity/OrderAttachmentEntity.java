package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 订单附件表
 * 
 * @Date: 2019年12月14日 11:43:05 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_order_attachment")
public class OrderAttachmentEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单编号
	 */
	private String orderNo;
	
	/**
	 * 订单类型  1-会员升级 2-工商注册 3-开票
	 */
	private Integer orderType;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 附件地址
	 */
	private String attachmentAddr;
	
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
