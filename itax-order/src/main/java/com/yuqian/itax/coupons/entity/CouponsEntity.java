package com.yuqian.itax.coupons.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 优惠卷表
 * 
 * @Date: 2021年04月08日 10:43:32 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_coupons")
public class CouponsEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 优惠卷编码
	 */
	private String couponsCode;
	/**
	 * 优惠卷名称
	 */
	private String couponsName;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 面额
	 */
	private Long faceAmount;
	
	/**
	 * 可用范围，多个之间用户逗号分割  1-个体户注册 2-开票 3-注销 4-个体户续费
	 */
	private String usableRange;
	
	/**
	 * 生效日期
	 */
	private Date startDate;
	
	/**
	 * 截止日期
	 */
	private Date endDate;
	
	/**
	 * 状态 0-未生效 1-已生效 2-已过期 3-已作废 4-已暂停
	 */
	private Integer status;
	
	/**
	 * 描述
	 */
	private String description;
	
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
