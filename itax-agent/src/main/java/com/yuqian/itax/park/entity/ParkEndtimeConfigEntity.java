package com.yuqian.itax.park.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区操作截止时间配置
 * 
 * @Date: 2021年03月16日 14:49:30 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_endtime_config")
public class ParkEndtimeConfigEntity implements Serializable {
	
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
	 * 操作类型 1-创建开票提示 2-开票记录自动开票
	 */
	private Integer operType;
	
	/**
	 * 发票方式 1-纸质发票 2-电子发票
	 */
	private Integer invoiceWay;
	
	/**
	 * 提示开始时间
	 */
	private Date startTime;
	
	/**
	 * 提示结束时间
	 */
	private Date endTime;
	
	/**
	 * 提示内容
	 */
	private String content;

	/**
	 * 年
	 */
	private Integer year;

	/**
	 * 季度
	 */
	private Integer quarter;
	
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
