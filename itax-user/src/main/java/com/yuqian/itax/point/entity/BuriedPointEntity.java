package com.yuqian.itax.point.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 埋点表
 * 
 * @Date: 2021年04月08日 10:46:03 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_buried_point")
public class BuriedPointEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 操作平台 1-微信小程序 2-支付宝小程序 3-运营平台
	 */
	private Integer operPlatform;
	
	/**
	 * 操作功能 1-banner
	 */
	private Integer operFunction;
	
	/**
	 * 用户类型 1-会员 2-非会员
	 */
	private Integer userType;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 资源id ,根据操作功能获取对应的资源id
	 */
	private Long sourceId;
	
	/**
	 * 操作时间
	 */
	private Date operTime;
	
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
