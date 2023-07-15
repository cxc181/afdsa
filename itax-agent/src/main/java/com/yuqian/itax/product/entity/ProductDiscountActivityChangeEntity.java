package com.yuqian.itax.product.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 产品特价活动变更表
 * 
 * @Date: 2021年07月15日 15:47:43 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_product_discount_activity_change")
public class ProductDiscountActivityChangeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 特价活动id
	 */
	private Long discountActivityId;
	
	/**
	 * 活动名称
	 */
	private String activityName;
	
	/**
	 * 产品类型 1-个体开户 5-个体开票 11-个体注销  15-公户申请和托管 16-个体托管费续费
	 */
	private Integer productType;
	
	/**
	 * 特价金额(分)
	 */
	private Long specialPriceAmount;
	
	/**
	 * 状态 0-待上架 1-已上架 2-已下架 3-已暂停
	 */
	private Integer status;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 活动开始日期
	 */
	private Date activityStartDate;
	
	/**
	 * 活动结束日期
	 */
	private Date activityEndDate;
	
	/**
	 * 人群标签id，多个id直接用逗号分割
	 */
	private String crowdLabelIds;
	
	/**
	 * 园区id列表,多个id之间用逗号分割
	 */
	private String parkIds;
	
	/**
	 * 行业id列表,多个id之间用逗号分割
	 */
	private String industryIds;
	
	/**
	 * 服务费阶梯列表，json对象
	 */
	private String chargeStandardJson;
	
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
