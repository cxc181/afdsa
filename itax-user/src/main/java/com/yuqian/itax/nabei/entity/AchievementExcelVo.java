package com.yuqian.itax.nabei.entity;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * 纳呗佣金提现业绩证明excel文件
 */
@Getter
@Setter
public class AchievementExcelVo implements Serializable {

	private static final long serialVersionUID = -4784474197451618211L;

	/**
	 * 序号
	 */
	@Excel(name = "序号")
	private String sequence;

	/**
	 * 凭证号
	 */
	@Excel(name = "凭证号")
	private String profitsNo;

	/**
	 * 姓名
	 */
	@Excel(name = "姓名")
	private String name;

	/**
	 * 身份证号
	 */
	@Excel(name = "身份证号")
	private String idCardNo;

	/**
	 * 单价
	 */
	@Excel(name = "单价")
	private String profitsAmount;
}
