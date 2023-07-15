package com.yuqian.itax.park.entity.vo;

import com.yuqian.itax.park.entity.ParkAgentAccountEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 园区经办人账号表
 * 
 * @Date: 2020年03月04日 09:44:53 
 * @author 蒋匿
 */
@Getter
@Setter
public class ParkAgentAccountVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	private Long id;
	
	/**
	 * 园区id
	 */
	private Long parkId;
	
	/**
	 * 经办人姓名
	 */
	private String agentName;
	
	/**
	 * 经办人账号
	 */
	private String agentAccount;

	/**
	 * 身份证号码
	 */
	private String idCardNo;

	/**
	 * 身份证正面照地址
	 */
	private String idCardFront;

	/**
	 * 身份证反面照地址
	 */
	private String idCardBack;

	/**
	 * 状态 0-禁用 1-正常  2-已删除
	 */
	private Integer status;

	/**
	 * 身份证正面照地址
	 */
	private String idCardFrontImg;

	/**
	 * 身份证反面照地址
	 */
	private String idCardBackImg;

	public ParkAgentAccountVO() {

	}

	public ParkAgentAccountVO(ParkAgentAccountEntity entity) {
		this.id = entity.getId();
		this.parkId = entity.getParkId();
		this.agentName = entity.getAgentName();
		this.agentAccount = entity.getAgentAccount();
		this.idCardNo = entity.getIdCardNo();
		this.idCardFront = entity.getIdCardFront();
		this.idCardBack = entity.getIdCardBack();
		this.status = entity.getStatus();
	}
}
