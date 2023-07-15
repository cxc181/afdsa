package com.yuqian.itax.park.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区经营地址生成规则
 * 
 * @Date: 2020年05月18日 16:42:07 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_business_address_rules")
public class ParkBusinessAddressRulesEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 注册前缀
	 */
	private String registPrefix;
	
	/**
	 * 当前注册区域
	 */
	private String currentRegistArea;
	
	/**
	 * 注册单位
	 */
	private String registUnit;
	
	/**
	 * 当前注册数
	 */
	private Integer currentRegistNum;
	
	/**
	 * 注册区域最小值
	 */
	private String registAreaMin;
	
	/**
	 * 注册区域最大值
	 */
	private String registAreaMax;
	
	/**
	 * 区域注册数最小值
	 */
	private Integer areaRegistNumMin;
	
	/**
	 * 区域注册数最大值
	 */
	private Integer areaRegistNumMax;
	
	/**
	 * 区域注册数长度
	 */
	private Integer areaRegistNumLen;

	/**
	 * 地址后缀
	 */
	private String registPostfix;

	/**
	 * 地址类型1-固定经营地址 2-按房间号自动递增
	 */
	private Integer addressType;

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
