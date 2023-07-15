package com.yuqian.itax.user.entity.vo;

import com.yuqian.itax.user.entity.MemberLevelEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 会员等级管理
 * 
 * @Date: 2019年12月07日 20:48:00 
 * @author 蒋匿
 */
@Getter
@Setter
public class MemberLevelVO implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * 等级名称
	 */
	private String levelName;
	
	/**
	 * 等级标识 -1-员工 0-普通会员  1-VIP 2-白银会员 3-税务顾问 4-铂金会员 5-城市服务商
	 */
	private Integer levelNo;


	/**
	 * 个体注册数
	 */
	private int registCompanyNum;

	/**
	 * 开票最低金额
	 */
	private Long invoiceMinMoney;

	/**
	 * 开票达标个体数
	 */
	private int completeInvoiceCompanyNum;


	public MemberLevelVO() {

	}
	public MemberLevelVO(MemberLevelEntity entity) {
		this.id = entity.getId();
		this.levelName = entity.getLevelName();
		this.levelNo = entity.getLevelNo();
		this.registCompanyNum = entity.getRegistCompanyNum();
		this.invoiceMinMoney = entity.getInvoiceMinMoney();
		this.completeInvoiceCompanyNum = entity.getCompleteInvoiceCompanyNum();
	}
}
