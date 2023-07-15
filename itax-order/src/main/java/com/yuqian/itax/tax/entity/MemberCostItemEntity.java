package com.yuqian.itax.tax.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 会员成本项表
 * 
 * @Date: 2022年03月10日 10:42:35 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_member_cost_item")
public class MemberCostItemEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 成本项名称
	 */
	private String costItemName;
	
	/**
	 * 成本项比例
	 */
	private BigDecimal costItemRate;
	
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
