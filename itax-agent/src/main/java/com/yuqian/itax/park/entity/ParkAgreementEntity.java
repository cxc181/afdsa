package com.yuqian.itax.park.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 园区协议表
 * 
 * @Date: 2020年07月14日 16:50:43 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_agreement")
public class ParkAgreementEntity implements Serializable {
	
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
	 * 园区编码
	 */
	private String parkCode;
	
	/**
	 * 协议编号
	 */
	private String agreementCode;

	/**
	 * 协议名称
	 */
	private String agreementName;

	/**
	 * 协议访问地址
	 */
	private String agreementViewUrl;
	
	/**
	 * 协议模板地址
	 */
	private String agreementTmplUrl;
	
	/**
	 * 状态 0-不可用 1-可用
	 */
	private Integer status;
	
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
