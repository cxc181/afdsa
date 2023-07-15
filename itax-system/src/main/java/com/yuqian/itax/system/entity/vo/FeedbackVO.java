package com.yuqian.itax.system.entity.vo;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 意见反馈
 * 
 * @Date: 2019年12月08日 20:38:54 
 * @author 蒋匿
 */
@Getter
@Setter
public class FeedbackVO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 用户姓名（优先显示实名姓名，没有则显示昵称）
	 */
	@Excel(name = "用户姓名", width = 15)
	private String userName;

	/**
	 * 用户手机号
	 */
	@Excel(name = "用户手机号", width = 16)
	private String memberPhone;

	/**
	 * 是否已实名 0-未认证 1-认证成功 2-认证失败
	 */
	@Excel(name = "是否已实名", replace = { "否_0","是_1","实名失败_2"}, height = 10, width = 15)
	private Integer authStatus;

	/**
	 * 反馈时间
	 */
	@Excel(name = "反馈时间", replace = {
			"-_null" }, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd HH:mm:ss", height = 10, width = 20)
	private Date addTime;

	/**
	 * 意见内容
	 */
	@Excel(name = "反馈内容", width = 20)
	private String feedbackContent;

	/**
	 * 所属OEM
	 */
	@Excel(name = "所属OEM", width = 15)
	private String oemName;
	
	
}
