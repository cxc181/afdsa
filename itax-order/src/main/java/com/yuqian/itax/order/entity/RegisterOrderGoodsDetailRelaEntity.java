package com.yuqian.itax.order.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 注册订单与商品明细的关系表
 * 
 * @Date: 2022年12月29日 13:51:42 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_register_order_goods_detail")
public class RegisterOrderGoodsDetailRelaEntity implements Serializable {
	
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
	 * 商品名称
	 */
	private String goodsName;
	
	/**
	 * 税收分类编码
	 */
	private String taxClassificationCode;
	
	/**
	 * 园区经营范围id
	 */
	private Long parkBusinessscopeId;
	
	/**
	 * 经营范围名称
	 */
	private String businessscopeName;
	
	/**
	 * 经营范围基础库id
	 */
	private Long businessscopeBaseId;
	
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
