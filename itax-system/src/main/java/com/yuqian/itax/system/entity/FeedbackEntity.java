package com.yuqian.itax.system.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 意见反馈
 * 
 * @Date: 2019年12月08日 20:38:54 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_feedback")
public class FeedbackEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 来源  1-小程序 2-安卓 3-ios
	 */
	private Integer source;
	
	/**
	 * 意见内容
	 */
	private String feedbackContent;
	
	/**
	 * 处理结果
	 */
	private String handingResult;
	
	/**
	 * 处理状态
	 */
	private Integer handingState;
	
	/**
	 * 处理人
	 */
	private Long handingUserId;
	
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
