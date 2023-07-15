package com.yuqian.itax.park.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 
 * 
 * @Date: 2022年10月11日 11:08:41 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_reward_policy_label")
public class ParkRewardPolicyLabelEntity implements Serializable {
	
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
	 * 奖励标签
	 */
	private String rewardLabel;
	
	/**
	 * 奖励说明
	 */
	private String rewardDesc;

	/**
	 * 基础奖励政策id
	 */
	private Long rewardLabelBaseId;
	
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
