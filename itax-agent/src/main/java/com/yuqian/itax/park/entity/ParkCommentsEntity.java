package com.yuqian.itax.park.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 园区评价表
 * 
 * @Date: 2022年10月11日 11:09:51 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_park_comments")
public class ParkCommentsEntity implements Serializable {
	
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
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 用户评分
	 */
	private BigDecimal userRatings;
	
	/**
	 * 评论内容
	 */
	private String commentsContent;
	
	/**
	 * 评论机构编码
	 */
	private String oemCode;
	
	/**
	 * 评论状态  1-可见 2-屏蔽
	 */
	private Integer status;
	
	/**
	 * 回复内容
	 */
	private String replyContent;
	
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
