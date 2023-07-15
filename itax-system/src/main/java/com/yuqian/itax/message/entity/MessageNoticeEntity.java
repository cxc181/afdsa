package com.yuqian.itax.message.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
/**
 * 消息通知表
 * 
 * @Date: 2020年03月04日 09:32:33 
 * @author 蒋匿
 */
@Getter
@Setter
@Table(name="t_e_message_notice")
public class MessageNoticeEntity implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * 主键id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 通知id
	 */
	private Long noticeId;
	
	/**
	 * 机构编码
	 */
	private String oemCode;
	
	/**
	 * 通知类型  1-短信通知 2-站内通知
	 */
	private Integer noticeType;

	/**
	 * 是否已弹窗 0-未弹窗 1-已弹窗
	 */
	private Integer isAlert;
	
	/**
	 * 通知位置(多个通知位置之间用逗号分割)  1-消息中心 2-首页弹窗
	 */
	private String noticePosition;
	
	/**
	 * 打开方式 1-通知详情 2-h5地址链接 3-小程序功能
	 */
	private Integer openMode;
	
	/**
	 * 业务类型 1-开户待支付 2-开户已完成 3-开票 4-用章 5-开票流水审核 6-待身份验证 7-托管费到期提醒 8-工商注册确认登记 9-工商注册用户未提交签名
	 * 10-对公户年费到期提醒 11-作废/红冲 12-作废重开 13-确认成本提醒
	 */
	private Integer businessType;
	
	/**
	 * 通知标题
	 */
	private String noticeTitle;
	
	/**
	 * 通知内容
	 */
	private String noticeContent;

	/**
	 * 副标题
	 */
	private String noticeSubtitle;
	
	/**
	 * 跳转地址
	 */
	private String jumpUrl;
	
	/**
	 * 用户手机号
	 */
	private String userPhones;
	
	/**
	 * 状态 0-未读1-已读 2-已下线 3-已取消
	 */
	private Integer status;
	
	/**
	 * 资源id
	 */
	private Long sourceId;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户类型 1-会员 2-后端用户
	 */
	private Integer userType;
	
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
