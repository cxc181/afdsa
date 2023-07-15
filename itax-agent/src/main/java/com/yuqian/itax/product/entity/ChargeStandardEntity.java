package com.yuqian.itax.product.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 收费标准
 * 
 * @Date: 2019年12月07日 20:42:23 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_charge_standard")
public class ChargeStandardEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 费用类型 1-服务费 
	 */
	private Integer chargeType;
	
	/**
	 * 费用金额最小
	 */
	private Long chargeMin;
	
	/**
	 * 费用金额最大
	 */
	private Long chargeMax;
	
	/**
	 * 收费比率
	 */
	private BigDecimal chargeRate;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 产品id
	 */
	private Long productId;
	
	/**
	 * 排序字段
	 */
	private Integer orderSn;
	
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

	/**
	 * 园区产品定价id
	 */
	private Long parkProductId;
	
}
