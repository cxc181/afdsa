package com.yuqian.itax.agent.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 活动管理
 * 
 * @Date: 2019年12月07日 20:31:10 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_activity")
public class ActivityEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 活动名称
	 */
	private String activityName;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 开始时间
	 */
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	private Date endTime;
	
	/**
	 * 状态 0-未开启 1-已开启 2-已暂停 3-已下线 4-已删除
	 */
	private Integer status;
	
	/**
	 * 活动规则
	 */
	private String activityRules;
	
	/**
	 * 活动方式 1-固定值 2-比率
	 */
	private Integer activityWay;
	
	/**
	 * 活动折扣
	 */
	private Long activityDiscount;
	
	/**
	 * 活动产品类型 1-工商开户 2-开票 3-会费
	 */
	private Integer activityProductType;
	
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
