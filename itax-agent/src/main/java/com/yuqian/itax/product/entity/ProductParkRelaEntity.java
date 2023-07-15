package com.yuqian.itax.product.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 产品与园区的关系
 * 
 * @Date: 2019年12月07日 20:43:06 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_r_product_park")
public class ProductParkRelaEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 产品id
	 */
	private Long productId;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
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
	 * 企业类型1-个体工商户 2-个人独资企业 3-有限合伙 4-有限责任
	 */
	private Integer companyType;

	/**
	 * 流程标记 1：标准视频认证流程，2：确认身份验证开启流程，3：工商局签名流程
	 */
	private Integer processMark;
}
