package com.yuqian.itax.order.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 订单微信授权关系表
 * 
 * @Date: 2021年03月16日 14:52:21 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_order_wechat_auth")
public class OrderWechatAuthRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 微信模板类型 1-工单审核 2-邀请签名 3-签名确认结果
	 */
	private Integer wechatTmplType;
	
	/**
	 * 微信模板id
	 */
	private Long wechatTmplId;
	
	/**
	 * 授权状态 0-未授权 1-已授权
	 */
	private Integer authStatus;
	
	/**
	 * 微信通知结果
	 */
	private String wechatResult;
	
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
